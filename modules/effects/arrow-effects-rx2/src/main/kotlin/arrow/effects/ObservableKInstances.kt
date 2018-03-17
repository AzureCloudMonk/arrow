package arrow.effects

import arrow.Kind
import arrow.core.Either
import arrow.core.identity
import arrow.effects.continuations.AsyncContinuation
import arrow.instance
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadError
import arrow.typeclasses.continuations.BindingCatchContinuation
import arrow.typeclasses.continuations.BindingContinuation
import kotlin.coroutines.experimental.CoroutineContext

@instance(ObservableK::class)
interface ObservableKApplicativeErrorInstance :
        ObservableKApplicativeInstance,
        ApplicativeError<ForObservableK, Throwable> {
    override fun <A> raiseError(e: Throwable): ObservableK<A> =
            ObservableK.raiseError(e)

    override fun <A> handleErrorWith(fa: ObservableKOf<A>, f: (Throwable) -> ObservableKOf<A>): ObservableK<A> =
            fa.handleErrorWith { f(it).fix() }
}

@instance(ObservableK::class)
interface ObservableKMonadInstance : Monad<ForObservableK> {
    override fun <A, B> ap(fa: ObservableKOf<A>, ff: ObservableKOf<kotlin.Function1<A, B>>): ObservableK<B> =
            fa.fix().ap(ff)

    override fun <A, B> flatMap(fa: ObservableKOf<A>, f: kotlin.Function1<A, ObservableKOf<B>>): ObservableK<B> =
            fa.fix().flatMap(f)

    override fun <A, B> flatMapIn(context: CoroutineContext, fa: Kind<ForObservableK, A>, f: (A) -> Kind<ForObservableK, B>): Kind<ForObservableK, B> =
            fa.fix().flatMapIn(context, f)

    override fun <A, B> map(fa: ObservableKOf<A>, f: kotlin.Function1<A, B>): ObservableK<B> =
            fa.fix().map(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, ObservableKOf<arrow.core.Either<A, B>>>): ObservableK<B> =
            ObservableK.tailRecM(a, f)

    override fun <A> pure(a: A): ObservableK<A> =
            ObservableK.pure(a)

    override fun <B> binding(context: CoroutineContext, c: suspend BindingContinuation<ForObservableK, *>.() -> B): ObservableK<B> =
            AsyncContinuation.binding(::identity, ObservableK.async(), context, c).fix()
}

@instance(ObservableK::class)
interface ObservableKMonadErrorInstance :
        ObservableKApplicativeErrorInstance,
        ObservableKMonadInstance,
        MonadError<ForObservableK, Throwable> {
    override fun <A, B> ap(fa: ObservableKOf<A>, ff: ObservableKOf<(A) -> B>): ObservableK<B> =
            super<ObservableKMonadInstance>.ap(fa, ff)

    override fun <A, B> map(fa: ObservableKOf<A>, f: (A) -> B): ObservableK<B> =
            super<ObservableKMonadInstance>.map(fa, f)

    override fun <A> pure(a: A): ObservableK<A> =
            super<ObservableKMonadInstance>.pure(a)

    override fun <B> bindingCatch(context: CoroutineContext, catch: (Throwable) -> Throwable, c: suspend BindingCatchContinuation<ForObservableK, Throwable, *>.() -> B): ObservableK<B> =
            AsyncContinuation.binding(::identity, ObservableK.async(), context, c).fix()
}

@instance(ObservableK::class)
interface ObservableKMonadSuspendInstance :
        ObservableKMonadErrorInstance,
        MonadSuspend<ForObservableK, Throwable> {
    override fun catch(catch: Throwable): Throwable =
            catch

    override fun <A> defer(fa: () -> ObservableKOf<A>): ObservableK<A> =
            ObservableK.defer(fa)
}

@instance(ObservableK::class)
interface ObservableKAsyncInstance :
        ObservableKMonadSuspendInstance,
        Async<ForObservableK, Throwable> {
    override fun <A> async(fa: Proc<A>): ObservableK<A> =
            ObservableK.runAsync(fa)

    override fun <B> binding(context: CoroutineContext, c: suspend BindingContinuation<ForObservableK, *>.() -> B): ObservableK<B> =
            super<ObservableKMonadSuspendInstance>.binding(context, c)

    override fun <B> bindingCatch(context: CoroutineContext, catch: (Throwable) -> Throwable, c: suspend BindingCatchContinuation<ForObservableK, Throwable, *>.() -> B): ObservableK<B> =
            super<ObservableKMonadSuspendInstance>.bindingCatch(context, catch, c)
}

@instance(ObservableK::class)
interface ObservableKEffectInstance :
        ObservableKAsyncInstance,
        Effect<ForObservableK, Throwable> {
    override fun <A> runAsync(fa: ObservableKOf<A>, cb: (Either<Throwable, A>) -> ObservableKOf<Unit>): ObservableK<Unit> =
            fa.fix().runAsync(cb)
}
