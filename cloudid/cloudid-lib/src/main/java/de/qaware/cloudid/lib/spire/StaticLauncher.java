package de.qaware.cloudid.lib.spire;

import de.qaware.cloudid.lib.util.Reflection;
import de.qaware.cloudid.lib.util.config.Props;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

/**
 * Launcher starting the SPIRE Agent connection in a static context.
 * <p>
 * Instantiates the bundle supplier factory defined by {@link Config#BUNDLE_SUPPLIER_FACTORY_CLASS}
 * and starts the life cycle.
 */
@Slf4j
@UtilityClass
public class StaticLauncher {

    private static BundleSupplierFactory bundleSupplierFactory;

    /**
     * Get the bundle supplier, starting the SPIRE Agent connection if necessary.
     *
     * @return bundle supplier
     */
    public static synchronized Supplier<Bundle> getBundleSupplier() {
        if (bundleSupplierFactory == null) {
            bundleSupplierFactory = createBundleSupplierFactory();
        }

        return bundleSupplierFactory.get();
    }

    /**
     * Reset the static launcher.
     *
     * Should only be used for tests.
     */
    public static synchronized void reset() {
        LOGGER.warn("Resetting {}", StaticLauncher.class.getSimpleName());
        bundleSupplierFactory = null;
    }

    private static BundleSupplierFactory createBundleSupplierFactory() {
        // Log the configuration on DEBUG
        Props.debugLog(Config.class);

        try {
            return Reflection.instantiate(Config.BUNDLE_SUPPLIER_FACTORY_CLASS.get());
        } catch (IllegalArgumentException e) {
            // Log the exception here as it has been observed to be dropped when called from Java Security API
            // implementations.
            LOGGER.error("Error instantiating the bundle supplier factory", e);
            throw e;
        }
    }

}
