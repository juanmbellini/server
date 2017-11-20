package ar.edu.itba.iot.carne_iot.server.services;

import ar.edu.itba.iot.carne_iot.server.models.Device;
import ar.edu.itba.iot.carne_iot.server.models.DeviceRegistration;
import ar.edu.itba.iot.carne_iot.server.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Defines behaviour of the service in charge of managing {@link Device}s and {@link DeviceRegistration}s.
 */
public interface DeviceService {

    /**
     * List all {@link Device}s stored in the system, in a paginated view.
     *
     * @param pageable The {@link Pageable} object containing pagination stuff.
     * @return The resulting {@link Page}.
     */
    Page<RegisteredDeviceWrapper> listDevices(Pageable pageable);

    /**
     * Gets a specific {@link Device}, together with registration data.
     *
     * @param deviceId The id of the {@link Device} being retrieved.
     * @return A nullable {@link Optional} of {@link Device}
     * containing the {@link Device} with the given {@code deviceId}, or empty if not present.
     */
    Optional<RegisteredDeviceWrapper> getDeviceWithRegistrationData(long deviceId);

    /**
     * Creates a new {@link Device} in the system.
     *
     * @return The created {@link Device}.
     */
    Device createDevice(long deviceId);

    /**
     * List {@link Device}s belonging to a given {@link ar.edu.itba.iot.carne_iot.server.models.User},
     * in a paginated view.
     *
     * @param ownerId  The id of the {@link ar.edu.itba.iot.carne_iot.server.models.User}
     *                 whose {@link Device}s are being returned.
     * @param pageable The {@link Pageable} object containing pagination stuff.
     * @return The resulting {@link Page}.
     */
    Page<DeviceWithNicknameWrapper> listUserDevices(long ownerId, Pageable pageable);

    /**
     * Gets a specific {@link Device} if registered.
     *
     * @param ownerId  The id of the {@link User} that owns the device.
     * @param deviceId The id of the {@link Device} being retrieved.
     * @return A nullable {@link Optional} of {@link Device}
     * containing the {@link Device} with the given {@code deviceId}, or empty if not present or not registered.
     */
    Optional<DeviceWithNicknameWrapper> getRegisteredDevice(long ownerId, long deviceId);

    /**
     * Sets the given {@code nickname} to the {@link Device} with the given id,
     * belonging to the {@link User} with the given {@code ownerId}.
     *
     * @param ownerId  The id of the {@link User} that owns the device.
     * @param deviceId The id of the {@link Device} being retrieved.
     * @param nickname The new nickname for the device.
     */
    void setNickname(long ownerId, long deviceId, String nickname);

    /**
     * Removes the nickname to the {@link Device} with the given id,
     * belonging to the {@link User} with the given {@code ownerId}.
     *
     * @param ownerId  The id of the {@link User} that owns the device.
     * @param deviceId The id of the {@link Device} being retrieved.
     * @apiNote This is an idempotent action.
     */
    void deleteNickname(long ownerId, long deviceId);

    /**
     * Registers a {@link Device} to a given {@link User}
     *
     * @param ownerId  The id of the {@link User} to which the device is being registered.
     * @param deviceId The id of the {@link Device} being registered.
     */
    void registerDevice(long ownerId, long deviceId);

    /**
     * Unregisters a {@link Device}.
     *
     * @param deviceId The id of the {@link Device} being unregistered.
     */
    void unregisterDevice(long deviceId);

    /**
     * Performs pairing of {@link ar.edu.itba.iot.carne_iot.server.models.Device}s.
     *
     * @param ownerId  The id of the {@link ar.edu.itba.iot.carne_iot.server.models.User}
     *                 owning the {@link ar.edu.itba.iot.carne_iot.server.models.Device}.
     * @param deviceId The id of the {@link ar.edu.itba.iot.carne_iot.server.models.Device} to be paired.
     * @return The token generated by the pairing process.
     */
    String pair(long ownerId, long deviceId);

    /**
     * Makes a {@link Device} start cooking (i.e change its state to {@link Device.State#ACTIVE}).
     * This is an idempotent operation.
     *
     * @param deviceId The id of the {@link Device} that will start cooking.
     */
    void startCooking(long deviceId);

    /**
     * Makes a {@link Device} stop cooking (i.e change its state to {@link Device.State#IDLE}).
     * This is an idempotent operation.
     *
     * @param deviceId The id of the {@link Device} that will stop cooking.
     */
    void stopCooking(long deviceId);

    /**
     * Updates the temperature of a given {@link Device}.
     *
     * @param deviceId    The id of the device being updated.
     * @param temperature The new temperature for the device.
     */
    void updateTemperature(long deviceId, BigDecimal temperature);


    /**
     * Wrapper class that holds {@link Device} data, possibly together with a {@link User} owning the Device.
     */
    class RegisteredDeviceWrapper {

        /**
         * The wrapped device.
         */
        private final Device device;

        /**
         * The wrapped user (must be null if the {@link #device} is not registered.
         */
        private final User user;

        /**
         * Constructor for registered devices.
         *
         * @param device The wrapped device.
         * @param user   The wrapped user (must be null if the {@code device} is not registered.
         */
        /* package */ RegisteredDeviceWrapper(Device device, User user) {
            this.device = device;
            this.user = user;
        }

        /**
         * Constructor for not registered device.
         *
         * @param device The wrapped device.
         */
        /* package */ RegisteredDeviceWrapper(Device device) {
            this(device, null);
        }

        /**
         * @return The wrapped device.
         */
        public Device getDevice() {
            return device;
        }

        /**
         * @return The wrapped user (will be null if the {@code device} is not registered.
         */
        public Optional<User> getUser() {
            return Optional.ofNullable(user);
        }
    }

    /**
     * Wrapper class that holds {@link Device} data together with the nickname given by its owner
     */
    class DeviceWithNicknameWrapper {

        /**
         * The wrapped device.
         */
        private final Device device;

        /**
         * The wrapped nickname.
         */
        private final String nickname;

        /**
         * Constructor.
         *
         * @param deviceRegistration The {@link DeviceRegistration} containing data to be wrapped.
         */
        /* package */ DeviceWithNicknameWrapper(DeviceRegistration deviceRegistration) {
            this.device = deviceRegistration.getDevice();
            this.nickname = deviceRegistration.getNickname();
        }

        /**
         * @return The wrapped device.
         */
        public Device getDevice() {
            return device;
        }

        /**
         * @return The wrapped nickname.
         */
        public String getNickname() {
            return nickname;
        }
    }
}
