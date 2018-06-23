package arrow.data

import arrow.core.Function1Of
import arrow.core.invoke
import arrow.instances.ForFunction1
import arrow.test.UnitSpec
import arrow.test.laws.MonadLaws
import arrow.typeclasses.Eq
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class Function1Test : UnitSpec() {
  val EQ: Eq<Function1Of<Int, Int>> = Eq { a, b ->
    a(1) == b(1)
  }

  init {
    ForFunction1<Int>() extensions {
      testLaws(MonadLaws.laws(this, EQ))
    }
  }
}
