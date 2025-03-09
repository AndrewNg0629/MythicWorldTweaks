package online.andrew2007.mythic.network;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import online.andrew2007.mythic.MythicWorldTweaks;
import online.andrew2007.mythic.config.RuntimeController;
import online.andrew2007.mythic.config.runtimeParams.TransmittableRuntimeParams;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class PlayConfigPushValidator {
    private static final ConcurrentHashMap<ServerPlayerEntity, ValidationInfo> pendingValidationPlayers = new ConcurrentHashMap<>();
    private static final ExecutorService threadExecutor = Executors.newCachedThreadPool();
    private static boolean executorShutDown = false;

    private static Object registerForValidation(ServerPlayerEntity serverPlayerEntity) {
        Object lock = new Object();
        pendingValidationPlayers.put(serverPlayerEntity, new ValidationInfo(lock));
        return lock;
    }

    public static void onConfigPushResponse(ServerPlayerEntity serverPlayerEntity, TransmittableRuntimeParams params) {
        ValidationInfo info = pendingValidationPlayers.get(serverPlayerEntity);
        if (info != null) {
            Object lock = info.getLock();
            synchronized (lock) {
                if (RuntimeController.getCurrentTParams().equals(params)) {
                    info.pass();
                } else {
                    info.reject("Config push failed, received config is not equivalent to the server's.");
                }
            }
        } else {
            throw new IllegalStateException(String.format("Player %s hasn't been registered.", serverPlayerEntity.getName()));
        }
    }

    public static void onConfigPush(ServerPlayerEntity serverPlayerEntity) {
        Runnable validationTask = () -> {
            Object lock = registerForValidation(serverPlayerEntity);
            synchronized (lock) {
                try {
                    lock.wait(3000);
                } catch (InterruptedException e) {
                    MythicWorldTweaks.LOGGER.error("Failed to validate player.", e);
                    serverPlayerEntity.networkHandler.disconnect(Text.of("Server error, failed to validate player."));
                }
            }
            ValidationInfo info = Objects.requireNonNull(pendingValidationPlayers.get(serverPlayerEntity));
            if (!info.isPassed()) {
                serverPlayerEntity.networkHandler.disconnect(Text.of(info.getFailReason()));
            }
            pendingValidationPlayers.remove(serverPlayerEntity);
        };
        threadExecutor.execute(validationTask);
    }

    public static void shutDownExecutor() {
        if (!executorShutDown) {
            threadExecutor.shutdown();
            threadExecutor.close();
            executorShutDown = true;
        }
    }

    private static class ValidationInfo {
        private final Object lock;
        private boolean validationPassed;
        private String failReason = "Config push response timed out, regarded as failure.";

        public ValidationInfo(@NotNull Object lock) {
            this.lock = Objects.requireNonNull(lock);
            this.validationPassed = false;
        }

        public Object getLock() {
            return this.lock;
        }

        public void pass() {
            this.validationPassed = true;
            lock.notifyAll();
        }

        public void reject(String failReason) {
            this.validationPassed = false;
            this.failReason = failReason;
            lock.notifyAll();
        }

        public boolean isPassed() {
            return this.validationPassed;
        }

        public String getFailReason() {
            return this.failReason;
        }
    }
}
