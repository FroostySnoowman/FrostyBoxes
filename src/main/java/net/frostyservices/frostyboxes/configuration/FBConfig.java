package net.frostyservices.frostyboxes.configuration;

import net.frostyservices.frostyboxes.configuration.types.ConfigMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FBConfig implements IDeepCloneable {

    private int cooldown = 5000;
    private boolean disableMovementCheck = false;

    private ConfigMessage prefix = new ConfigMessage("&8[&4&lNB&c&lB&8] &r");
    private String inventoryName = "%shulker_name%";
    private ConfigMessage cooldownMessage = new ConfigMessage("&cYou have to wait %seconds% before using this again!");

    @Override
    public IDeepCloneable clone() {
        return new FBConfig(
                cooldown,
                disableMovementCheck,
                prefix,
                inventoryName,
                cooldownMessage
        );
    }
}
