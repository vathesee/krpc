package krpc.trace.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import krpc.common.Json;
import krpc.common.NamedThreadFactory;
import krpc.httpclient.DefaultHttpClient;
import krpc.httpclient.HttpClientReq;
import krpc.httpclient.HttpClientRes;
import krpc.trace.Event;
import krpc.trace.Span;
import krpc.trace.Trace;
import krpc.trace.TraceAdapter;
import krpc.trace.TraceContext;

public class ZipkinTraceAdapter implements TraceAdapter {

	static Logger log = LoggerFactory.getLogger(ZipkinTraceAdapter.class);
	
	String postUrl;
	int queueSize = 10000;
	int retryCount = 3;
	int retryInterval = 1000;

	DefaultHttpClient hc;
	NamedThreadFactory threadFactory = new NamedThreadFactory("zipkin_report");
	ThreadPoolExecutor pool;

	public ZipkinTraceAdapter(Map<String,String> params) {

		postUrl = "http://"+params.get("server")+"/api/v2/spans";
		
		String s = params.get("queueSize");
		if( !isEmpty(s) ) queueSize = Integer.parseInt(s);
		
		s = params.get("retryCount");
		if( !isEmpty(s) ) retryCount = Integer.parseInt(s);

		s = params.get("retryInterval");
		if( !isEmpty(s) ) retryInterval = Integer.parseInt(s);
	}
	
	public void init() {
		hc = new DefaultHttpClient();
		hc.init();
		pool = new ThreadPoolExecutor(1,1, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(queueSize),threadFactory);
	}
	
	public void close() {
		if( hc == null ) return;
		pool.shutdownNow();
		pool = null;
		hc.close();
		hc = null;
	}
	
	public void send(TraceContext ctx, Span span) {
		try {
			pool.execute( new Runnable() {
				public void run() {
					try {
						report(ctx,span);
					} catch(Exception e) {
						log.error("zipkin report exception",e);
					}
				}
			});
		} catch(Exception e) {
			log.error("zipkin report queue is full");
		}
	}
	
	static class ZipkinAnnotation {
		long timestamp;
		String value;
		
		public long getTimestamp() {
			return timestamp;
		}
		public void setTimestamp(long timestamp) {
			this.timestamp = timestamp;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
	}
	
	static class ZipkinEndpoint {
		String serviceName;
		String ipv4;
		String ipv6; // never used
		int port;
		
		public String getServiceName() {
			return serviceName;
		}
		public void setServiceName(String serviceName) {
			this.serviceName = serviceName;
		}
		public String getIpv4() {
			return ipv4;
		}
		public void setIpv4(String ipv4) {
			this.ipv4 = ipv4;
		}
		public String getIpv6() {
			return ipv6;
		}
		public void setIpv6(String ipv6) {
			this.ipv6 = ipv6;
		}
		public int getPort() {
			return port;
		}
		public void setPort(int port) {
			this.port = port;
		}
	}
	
	static class ZipkinSpan {
		String traceId;
		String name;
		String parentId;
		String id;
		String kind;
		long timestamp;
		long duration;
		boolean debug = true; // todo
		boolean shared = true; // never used
		ZipkinEndpoint localEndpoint;  // never used
		ZipkinEndpoint remoteEndpoint;
		List<ZipkinAnnotation> annotations;
		Map<String,String> tags;
		
		public String getTraceId() {
			return traceId;
		}
		public void setTraceId(String traceId) {
			this.traceId = traceId;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getParentId() {
			return parentId;
		}
		public void setParentId(String parentId) {
			this.parentId = parentId;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getKind() {
			return kind;
		}
		public void setKind(String kind) {
			this.kind = kind;
		}
		public long getTimestamp() {
			return timestamp;
		}
		public void setTimestamp(long timestamp) {
			this.timestamp = timestamp;
		}
		public long getDuration() {
			return duration;
		}
		public void setDuration(long duration) {
			this.duration = duration;
		}
		public boolean isDebug() {
			return debug;
		}
		public void setDebug(boolean debug) {
			this.debug = debug;
		}
		public boolean isShared() {
			return shared;
		}
		public void setShared(boolean shared) {
			this.shared = shared;
		}
		public ZipkinEndpoint getLocalEndpoint() {
			return localEndpoint;
		}
		public void setLocalEndpoint(ZipkinEndpoint localEndpoint) {
			this.localEndpoint = localEndpoint;
		}
		public ZipkinEndpoint getRemoteEndpoint() {
			return remoteEndpoint;
		}
		public void setRemoteEndpoint(ZipkinEndpoint remoteEndpoint) {
			this.remoteEndpoint = remoteEndpoint;
		}
		public List<ZipkinAnnotation> getAnnotations() {
			return annotations;
		}
		public void setAnnotations(List<ZipkinAnnotation> annotations) {
			this.annotations = annotations;
		}
		public Map<String, String> getTags() {
			return tags;
		}
		public void setTags(Map<String, String> tags) {
			this.tags = tags;
		}
	}

	void report(TraceContext ctx, Span span) {
		List<ZipkinSpan> list = convert(ctx,span);
		String json = Json.toJson(list);
		HttpClientReq req = new HttpClientReq("POST",postUrl).setContent(json);
		int i=0;
		while(i<retryCount) {
			HttpClientRes res = hc.call(req);
			if( res.getRetCode() == 0 && res.getHttpCode() == 202 ) break; // success
			try { Thread.sleep(retryInterval); } catch(Exception e) { break; }
		}
	}

	List<ZipkinSpan> convert(TraceContext ctx, Span span) {
		List<ZipkinSpan> list = new ArrayList<>();
		convert(ctx,span,list);
		return list;
	}
	
	void convert(TraceContext ctx, Span span,List<ZipkinSpan> list) {
		ZipkinSpan zs = new ZipkinSpan();
		zs.traceId = ctx.getTraceId();
		zs.name = span.getAction();
		String rpcId = span.getRpcId();
		int p = rpcId.indexOf(":");
		zs.parentId = rpcId.substring(0, p);
		if( zs.parentId.equals("0") ) zs.parentId = "";
		zs.id = rpcId.substring(p+1);
		zs.kind = span.getType().equals("RPCSERVER") || span.getType().equals("HTTPSERVER") ?"SERVER":"CLIENT";
		zs.timestamp = ctx.getRequestTimeMicros() + ( span.getStartMicros() - ctx.getStartMicros() );
		zs.duration = span.getTimeUsedMicros();
		zs.tags = span.getTags();
		List<Event> events = span.getEvents();
		if( events != null ) {
			List<ZipkinAnnotation> zans = new ArrayList<>();
			for(Event e:events) {
				ZipkinAnnotation za = new ZipkinAnnotation();
				za.timestamp = ctx.getRequestTimeMicros() + ( e.getStartMicros() - ctx.getStartMicros() );
				za.value = e.toAnnotationString();
				zans.add(za);
			}
			zs.annotations = zans;
		}
		
		ZipkinEndpoint rep = new ZipkinEndpoint();
		if( span.getType().equals("RPCSERVER")  || span.getType().equals("HTTPSERVER") )
			rep.serviceName = ctx.getRemoteAppName();
		else if( span.getType().equals("RPCCLIENT") ) {
			//int p2 = span.getAction().indexOf(".");
			//rep.serviceName = span.getAction().substring(0,p2);
		} else 
			rep.serviceName = span.getType();

		String remoteAddr = span.getRemoteAddr();
		if( remoteAddr != null ) {
			p = remoteAddr.lastIndexOf(":");
			String s = remoteAddr.substring(0, p);
			if( s.indexOf(":") > 0 )
				rep.ipv4 = s;
			else 
				rep.ipv6 = s;
			rep.port = Integer.parseInt( remoteAddr.substring(p+1) );
		}
		zs.remoteEndpoint = rep;

		if( span.getType().equals("RPCSERVER")  || span.getType().equals("HTTPSERVER") ) {
			ZipkinEndpoint c = new ZipkinEndpoint();
			c.serviceName = Trace.getAppName();
			zs.localEndpoint = c;
		}
		
		list.add(zs);
		
		if( span.getChildren() != null ) {
			for(Span child:span.getChildren()) {
				convert(ctx, child, list);
			}
		}
	}

	public String newTraceId() {
		String s = UUID.randomUUID().toString();
	    return s.replaceAll("-", "");		
	}
	
	public String newZeroRpcId(boolean isServer) {
		if( isServer )
			return "0:"+nextSpanId();
		else 
			return "0:0";
	}
	
	public String newEntryRpcId(String parentRpcId) {
		return parentRpcId;
	}
	
	public String newChildRpcId(String parentRpcId,AtomicInteger subCalls) {
		int p = parentRpcId.indexOf(":");
		return parentRpcId.substring(p+1)+":"+nextSpanId(); // parentSpanId : spanId
	}
	
	private String nextSpanId() {
		String s = UUID.randomUUID().toString();
	    s =  s.replaceAll("-", "");				
		return s.substring(0,16);
	}

	boolean isEmpty(String s) {
		return s == null || s.isEmpty();
	}
	
} 