package config

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object TestUtil {

  implicit class CustomAwait[T](val f: Future[T]) extends AnyVal {

    def await: T = Await.result(f, 200.seconds)
  }

}
