package net.frostyservices.frostyboxes.util;

public enum FBPermission {
    OPEN_SHULKER("fb.use"),
    ADMIN("fb.admin"),
    BYPASS_COOLDOWN("fb.bypasscooldown");
    final String value;

    FBPermission(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
