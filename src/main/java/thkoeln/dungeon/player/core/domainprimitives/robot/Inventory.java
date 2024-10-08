package thkoeln.dungeon.player.core.domainprimitives.robot;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import thkoeln.dungeon.player.core.domainprimitives.DomainPrimitiveException;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResource;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResourceType;

import java.util.ArrayList;
import java.util.List;

@Getter
@Embeddable
@EqualsAndHashCode
public class Inventory {

    private Integer capacity;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<MineableResource> resources;

    protected Inventory() {}

    protected Inventory(Integer capacity, List<MineableResource> resources) {
        this.capacity = capacity;
        this.resources = resources;
    }

    public static Inventory fromCapacity(Integer capacity) {
        if (capacity == null || capacity <= 0) throw new DomainPrimitiveException("Invalid Parameter for Inventory!");
        List<MineableResource> resources = new ArrayList<>();
        for (MineableResourceType type : MineableResourceType.values()) {
            resources.add(MineableResource.empty(type));
        }
        return new Inventory(capacity, resources);
    }

    public static Inventory fromCapacityAndResources(Integer capacity, List<MineableResource> resources) {
        if (capacity == null || capacity <= 0 || resources == null) throw new DomainPrimitiveException("Invalid Parameter for Inventory!");
        return new Inventory(capacity, resources);
    }

    public boolean isEmpty() {
        return getUsedCapacity() == 0;
    }

    public boolean isFull() {
        return this.capacity.equals(getUsedCapacity());
    }

    public Integer getUsedCapacity() {
        Integer amount = 0;
        for (MineableResource resource : resources) {
            amount += resource.getAmount();
        }
        return amount;
    }

    private List<MineableResourceType> getResourceTypesInInventory() {
        List<MineableResourceType> types = new ArrayList<>();
        for (MineableResource resource : resources) {
            types.add(resource.getType());
        }
        return types;
    }

    public Inventory setMineableResource(MineableResource setResource) {
        List<MineableResource> mineableResourceList = new ArrayList<>();
        for (MineableResource resource: resources) {
            if (resource.getType() != setResource.getType())
                mineableResourceList.add(resource);
        }
        if (!setResource.isEmpty())
            mineableResourceList.add(setResource);
        return Inventory.fromCapacityAndResources(capacity, mineableResourceList);
    }
}
