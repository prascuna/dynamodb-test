package com.example

import java.util.concurrent.{Future => JFuture}

import com.amazonaws.AmazonWebServiceRequest
import com.amazonaws.handlers.AsyncHandler

import scala.concurrent.{Promise, Future => SFuture}

package object repositories {
  private[repositories] def wrapAsyncMethod[Request <: AmazonWebServiceRequest, Result](
                                                                                         f: (Request, AsyncHandler[Request, Result]) => JFuture[Result],
                                                                                         request: Request
                                                                                       ): SFuture[Result] = {
    val p = Promise[Result]
    f(request, promiseToAsyncHandler(p))
    p.future
  }

  private[repositories] def promiseToAsyncHandler[Request <: AmazonWebServiceRequest, Result](promise: Promise[Result]) = {
    new AsyncHandler[Request, Result] {
      override def onError(exception: Exception): Unit = promise.failure(exception)

      override def onSuccess(request: Request, result: Result): Unit = promise.success(result)
    }

  }

}
