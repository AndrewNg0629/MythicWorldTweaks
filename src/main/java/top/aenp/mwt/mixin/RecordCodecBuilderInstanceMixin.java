package top.aenp.mwt.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapEncoder;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

//This mixin is based on the latest version of Mojang DFU code, to fix the RecordCodecBuilder encoding order issue on this older DFU.

@Mixin(value = RecordCodecBuilder.Instance.class, remap = false)
public class RecordCodecBuilderInstanceMixin {
    @Inject(method = "lambda$lift1$1", at = @At(value = "RETURN"), cancellable = true, remap = false)
    private <A, R> void lambda1(CallbackInfoReturnable<MapEncoder.Implementation<R>> info, @Local(name = "fEnc") MapEncoder<Function<A, R>> fEnc, @Local(name = "aEnc") MapEncoder<A> aEnc, @Local(name = "aFromO") A aFromO) {
        info.setReturnValue(
                new MapEncoder.Implementation<>() {
                    @Override
                    public <T> RecordBuilder<T> encode(final R input, final DynamicOps<T> ops, final RecordBuilder<T> prefix) {
                        fEnc.encode(a1 -> input, ops, prefix);
                        aEnc.encode(aFromO, ops, prefix);
                        return prefix;
                    }

                    @Override
                    public <T> Stream<T> keys(final DynamicOps<T> ops) {
                        return Stream.concat(aEnc.keys(ops), fEnc.keys(ops));
                    }

                    @Override
                    public String toString() {
                        return fEnc + " * " + aEnc;
                    }
                }
        );
    }

    @Inject(method = "lambda$ap2$4", at = @At(value = "RETURN"), cancellable = true, remap = false)
    private <A, B, R> void lambda2(CallbackInfoReturnable<MapEncoder.Implementation<R>> info, @Local(name = "fEncoder") MapEncoder<BiFunction<A, B, R>> fEncoder, @Local(name = "aEncoder") MapEncoder<A> aEncoder, @Local(name = "aFromO") A aFromO, @Local(name = "bEncoder") MapEncoder<B> bEncoder, @Local(name = "bFromO") B bFromO) {
        info.setReturnValue(
                new MapEncoder.Implementation<>() {
                    @Override
                    public <T> RecordBuilder<T> encode(final R input, final DynamicOps<T> ops, final RecordBuilder<T> prefix) {
                        fEncoder.encode((a1, b1) -> input, ops, prefix);
                        aEncoder.encode(aFromO, ops, prefix);
                        bEncoder.encode(bFromO, ops, prefix);
                        return prefix;
                    }

                    @Override
                    public <T> Stream<T> keys(final DynamicOps<T> ops) {
                        return Stream.of(
                                fEncoder.keys(ops),
                                aEncoder.keys(ops),
                                bEncoder.keys(ops)
                        ).flatMap(Function.identity());
                    }

                    @Override
                    public String toString() {
                        return fEncoder + " * " + aEncoder + " * " + bEncoder;
                    }
                }
        );
    }

    @Inject(method = "lambda$ap3$6", at = @At(value = "RETURN"), cancellable = true, remap = false)
    private <T1, T2, T3, R> void lambda3(CallbackInfoReturnable<MapEncoder.Implementation<R>> info, @Local(name = "fEncoder") MapEncoder<Function3<T1, T2, T3, R>> fEncoder, @Local(name = "e1") MapEncoder<T1> e1, @Local(name = "v1") T1 v1, @Local(name = "e2") MapEncoder<T2> e2, @Local(name = "v2") T2 v2, @Local(name = "e3") MapEncoder<T3> e3, @Local(name = "v3") T3 v3) {
        info.setReturnValue(
                new MapEncoder.Implementation<>() {
                    @Override
                    public <T> RecordBuilder<T> encode(final R input, final DynamicOps<T> ops, final RecordBuilder<T> prefix) {
                        fEncoder.encode((t1, t2, t3) -> input, ops, prefix);
                        e1.encode(v1, ops, prefix);
                        e2.encode(v2, ops, prefix);
                        e3.encode(v3, ops, prefix);
                        return prefix;
                    }

                    @Override
                    public <T> Stream<T> keys(final DynamicOps<T> ops) {
                        return Stream.of(
                                fEncoder.keys(ops),
                                e1.keys(ops),
                                e2.keys(ops),
                                e3.keys(ops)
                        ).flatMap(Function.identity());
                    }

                    @Override
                    public String toString() {
                        return fEncoder + " * " + e1 + " * " + e2 + " * " + e3;
                    }
                }
        );
    }

    @Inject(method = "lambda$ap4$8", at = @At(value = "RETURN"), cancellable = true, remap = false)
    private <T1, T2, T3, T4, R> void lambda4(CallbackInfoReturnable<MapEncoder.Implementation<R>> info, @Local(name = "fEncoder") MapEncoder<Function4<T1, T2, T3, T4, R>> fEncoder, @Local(name = "e1") MapEncoder<T1> e1, @Local(name = "v1") T1 v1, @Local(name = "e2") MapEncoder<T2> e2, @Local(name = "v2") T2 v2, @Local(name = "e3") MapEncoder<T3> e3, @Local(name = "v3") T3 v3, @Local(name = "e4") MapEncoder<T4> e4, @Local(name = "v4") T4 v4) {
        info.setReturnValue(
                new MapEncoder.Implementation<>() {
                    @Override
                    public <T> RecordBuilder<T> encode(final R input, final DynamicOps<T> ops, final RecordBuilder<T> prefix) {
                        fEncoder.encode((t1, t2, t3, t4) -> input, ops, prefix);
                        e1.encode(v1, ops, prefix);
                        e2.encode(v2, ops, prefix);
                        e3.encode(v3, ops, prefix);
                        e4.encode(v4, ops, prefix);
                        return prefix;
                    }

                    @Override
                    public <T> Stream<T> keys(final DynamicOps<T> ops) {
                        return Stream.of(
                                fEncoder.keys(ops),
                                e1.keys(ops),
                                e2.keys(ops),
                                e3.keys(ops),
                                e4.keys(ops)
                        ).flatMap(Function.identity());
                    }

                    @Override
                    public String toString() {
                        return fEncoder + " * " + e1 + " * " + e2 + " * " + e3 + " * " + e4;
                    }
                }
        );
    }
}
