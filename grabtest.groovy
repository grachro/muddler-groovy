@GrabResolver(name='grachro', root='http://grachro.github.io/mvn-repo')
@Grapes([
@Grab(group='javax.servlet', module='javax.servlet-api', version='3.1.0'),
@Grab('org.eclipse.jetty:jetty-http:9.3.2.v20150730'),
@Grab('org.eclipse.jetty:jetty-io:9.3.2.v20150730'),
@Grab('com.grachro:muddler-groovy:0.0.9')
])
import com.grachro.muddler.Muddler

Muddler.main([] as String[])


//動きません
//
//>groovy grabtest.groovy
//
//java.lang.NoSuchMethodError: javax.servlet.http.HttpServletRequest.isAsyncSupported()Z
//	at org.eclipse.jetty.server.handler.ResourceHandler.handle(ResourceHandler.java:520)
//	at org.eclipse.jetty.server.handler.HandlerList.handle(HandlerList.java:52)
//	at org.eclipse.jetty.server.handler.HandlerWrapper.handle(HandlerWrapper.java:119)
//	at org.eclipse.jetty.server.Server.handle(Server.java:517)
//	at org.eclipse.jetty.server.HttpChannel.handle(HttpChannel.java:302)
//	at org.eclipse.jetty.server.HttpConnection.onFillable(HttpConnection.java:242)
//	at org.eclipse.jetty.io.AbstractConnection$ReadCallback.succeeded(AbstractConnection.java:245)
//	at org.eclipse.jetty.io.FillInterest.fillable(FillInterest.java:95)
//	at org.eclipse.jetty.io.SelectChannelEndPoint$2.run(SelectChannelEndPoint.java:75)
//	at org.eclipse.jetty.util.thread.strategy.ExecuteProduceConsume.produceAndRun(ExecuteProduceConsume.java:213)
//	at org.eclipse.jetty.util.thread.strategy.ExecuteProduceConsume.run(ExecuteProduceConsume.java:147)
//	at org.eclipse.jetty.util.thread.QueuedThreadPool.runJob(QueuedThreadPool.java:654)
//	at org.eclipse.jetty.util.thread.QueuedThreadPool$3.run(QueuedThreadPool.java:572)
//	at java.lang.Thread.run(Thread.java:745)
