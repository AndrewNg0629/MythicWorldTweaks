package online.andrew2007.config;

import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

@SuppressWarnings("unused")
public class RuntimeParams {
    public static final class RuntimeParam<P> {
        private final BiConsumer<P, P> onParamChange;
        private P currentParamValue;
        private P prevParamValue;

        public RuntimeParam(P initialValue, @Nullable BiConsumer<P, P> onParamChange) {
            this.currentParamValue = initialValue;
            this.onParamChange = onParamChange;
            if (this.onParamChange != null) {
                this.onParamChange.accept(this.prevParamValue, this.currentParamValue);
            }
        }

        public P getCurrentParamValue() {
            return this.currentParamValue;
        }

        public void setCurrentParamValue(P newParamValue) {
            this.prevParamValue = this.currentParamValue;
            this.currentParamValue = newParamValue;
            if (this.onParamChange != null) {
                this.onParamChange.accept(this.prevParamValue, this.currentParamValue);
            }
        }
    }
}