package io.vertx.core.eventbus.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.eventbus.impl.clustered.ClusteredMessage;

import java.util.concurrent.ConcurrentMap;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Segment;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.instrumentation.labs.vertx.MessageHeaders;
import com.newrelic.instrumentation.labs.vertx.NRMessageHandlerWrapper;
import com.newrelic.instrumentation.labs.vertx.NRWrappedReplyHandler;
import com.newrelic.instrumentation.labs.vertx.TokenUtils;

@SuppressWarnings({"rawtypes"})
@Weave(type=MatchType.BaseClass)
public abstract class EventBusImpl implements EventBus {

	protected final ConcurrentMap<String, Handlers> handlerMap = Weaver.callOriginal();

	@Trace
	public <T> MessageConsumer<T> consumer(String address, Handler<Message<T>> handler) {
		if(!NRMessageHandlerWrapper.class.isInstance(handler)) {
			NRMessageHandlerWrapper<T> wrapper = (NRMessageHandlerWrapper<T>)handler;
			handler = wrapper;
		}
		return Weaver.callOriginal();
	}
	
	@Trace
	public <T> MessageConsumer<T> localConsumer(String address, Handler<Message<T>> handler) {
		if(!NRMessageHandlerWrapper.class.isInstance(handler)) {
			NRMessageHandlerWrapper<T> wrapper = (NRMessageHandlerWrapper<T>)handler;
			handler = wrapper;
		}
		return Weaver.callOriginal();
	}
	

	@Trace
	public <T> EventBus send(String address, Object message, DeliveryOptions options, Handler<AsyncResult<Message<T>>> replyHandler) {
		if (replyHandler != null) {
			Token token = NewRelic.getAgent().getTransaction().getToken();
			Segment segment = NewRelic.getAgent().getTransaction().startSegment("EventBus-Send-" + address);
			NRWrappedReplyHandler<T> wrapper = new NRWrappedReplyHandler<T>(token, segment, replyHandler);
			replyHandler = wrapper;
		}
		if(!TokenUtils.tempAddress(address)) {
			NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","EventBusImpl","send",address});
		} else {
			NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","EventBusImpl","send","Temp"});
		}
		return Weaver.callOriginal();
	}

	@Trace
	protected <T> boolean deliverMessageLocally(MessageImpl msg) {
		String address = msg.address();
		if(!TokenUtils.tempAddress(address)) {
			NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","EventBusImpl","deliverMessageLocally",address});
		} else {
			NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","EventBusImpl","deliverMessageLocally","Temp"});
		}
		return Weaver.callOriginal();
	}

	@Trace
	protected MessageImpl createMessage(boolean send, String address, MultiMap headers, Object body, String codecName) {
		if(!TokenUtils.tempAddress(address)) {
			NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","EventBusImpl","createMessage",address});
		} else {
			NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","EventBusImpl","createMessage","Temp"});
		}
		MessageImpl msg = Weaver.callOriginal();
		if(!ClusteredMessage.class.isInstance(msg)) {
			Token token = NewRelic.getAgent().getTransaction().getToken();
			Integer hashCode = token.hashCode();
			MultiMap msgheaders = msg.headers();
			MessageHeaders headersWrapper = new MessageHeaders(msgheaders);
			NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(headersWrapper);

			TokenUtils.addToken(hashCode.toString(), token);
		}
		return msg;
	}

	@Trace(dispatcher=true)
	protected void callCompletionHandlerAsync(Handler<AsyncResult<Void>> completionHandler) {
		
		Weaver.callOriginal();
	}

	@Trace
	private <T> void deliverToHandler(MessageImpl msg, HandlerHolder<T> holder) {
		String address = msg.address();
		if(!TokenUtils.tempAddress(address)) {
			NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","EventBusImpl","deliverToHandler",address});
		} else {
			NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","EventBusImpl","deliverToHandler","Temp"});
		}

		if(holder.token == null) {
			holder.token = NewRelic.getAgent().getTransaction().getToken();
		}
		Weaver.callOriginal();
	}

	@Trace(dispatcher=true)
	public EventBus publish(String address, Object message, DeliveryOptions options) {
		if(!TokenUtils.tempAddress(address)) {
			NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","EventBusImpl","publish",address});
		} else {
			NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","EventBusImpl","publish","Temp"});
		}
		return Weaver.callOriginal();
	}

	@Trace(dispatcher=true)
	protected <T> void sendReply(SendContextImpl<T> sendContext, MessageImpl replierMessage) {
		String address = replierMessage.address();
		if(!TokenUtils.tempAddress(address)) {
			NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","EventBusImpl","sendReply",address});
		} else {
			NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","EventBusImpl","sendReply","Temp"});
		}
		Weaver.callOriginal();
	}

	@Trace(async=true)
	protected <T> void sendReply(MessageImpl replyMessage, MessageImpl replierMessage, DeliveryOptions options, Handler<AsyncResult<Message<T>>> replyHandler) {
		String address = replierMessage.address();
		if (replyHandler != null) {
			Token token = NewRelic.getAgent().getTransaction().getToken();
			Segment segment = NewRelic.getAgent().getTransaction().startSegment("EventBus-SendReply-" + address);
			NRWrappedReplyHandler<T> wrapper = new NRWrappedReplyHandler<T>(token, segment, replyHandler);
			replyHandler = wrapper;
		}
		if(!TokenUtils.tempAddress(address)) {
			NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","EventBusImpl","sendReply",address});
		} else {
			NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","EventBusImpl","sendReply","Temp"});
		}
		Weaver.callOriginal();
	}

	@Weave(type=MatchType.BaseClass)
	static abstract protected class SendContextImpl<T> {

		public final HandlerRegistration<T> handlerRegistration = Weaver.callOriginal();

		public abstract Message<T> message();

		public SendContextImpl(MessageImpl message, DeliveryOptions options, HandlerRegistration<T> handlerRegistration) {
		}

		@Trace
		public void next() {
			Weaver.callOriginal();
		}
	}


}
