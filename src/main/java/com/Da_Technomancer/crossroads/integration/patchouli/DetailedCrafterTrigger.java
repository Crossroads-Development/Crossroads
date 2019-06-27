package com.Da_Technomancer.crossroads.integration.patchouli;

import com.Da_Technomancer.crossroads.Main;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DetailedCrafterTrigger implements ICriterionTrigger<DetailedCrafterTrigger.Instance> {


	private static final ResourceLocation id = new ResourceLocation(Main.MODID, "detailed_crafter");
	private final Map<PlayerAdvancements, DetailedCrafterTrigger.Listeners> listeners = Maps.<PlayerAdvancements, DetailedCrafterTrigger.Listeners>newHashMap();

	public DetailedCrafterTrigger() {}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public void addListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<DetailedCrafterTrigger.Instance> listener) {
		DetailedCrafterTrigger.Listeners detailedcraftertrigger$listeners = this.listeners.get(playerAdvancementsIn);
		if (detailedcraftertrigger$listeners == null) {
			detailedcraftertrigger$listeners = new DetailedCrafterTrigger.Listeners(playerAdvancementsIn);
			this.listeners.put(playerAdvancementsIn,detailedcraftertrigger$listeners );
		}
		detailedcraftertrigger$listeners.add(listener);
	}

	@Override
	public void removeListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<DetailedCrafterTrigger.Instance> listener) {
		DetailedCrafterTrigger.Listeners detailedcraftertrigger$listeners = this.listeners.get(playerAdvancementsIn);
		if (detailedcraftertrigger$listeners != null) {
			detailedcraftertrigger$listeners.remove(listener);
			if (detailedcraftertrigger$listeners.isEmpty()) {
				this.listeners.remove(playerAdvancementsIn);
			}
		}
	}

	@Override
	public void removeAllListeners(PlayerAdvancements playerAdvancementsIn) {
		this.listeners.remove(playerAdvancementsIn);
	}

	@Override
	public Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
		PathPredicate pathPredicate = PathPredicate.deserialize(json);
		return new DetailedCrafterTrigger.Instance(this.id, pathPredicate);
	}

	public static class Instance extends AbstractCriterionInstance {
		private final PathPredicate pathPredicate;

		public Instance(ResourceLocation criterionIn, PathPredicate pathPredicate) {
			super(criterionIn);
			this.pathPredicate = pathPredicate;
		}
		public boolean test(NBTTagCompound tagCompound) {
			return this.pathPredicate.test(tagCompound);
		}
	}


	public void trigger(EntityPlayerMP player, NBTTagCompound tag) {
		DetailedCrafterTrigger.Listeners detailedcraftertrigger$listeners = this.listeners.get(player.getAdvancements());

		if (detailedcraftertrigger$listeners != null) {
			detailedcraftertrigger$listeners.trigger(tag);
		}
	}

	static class Listeners {
		private final PlayerAdvancements playerAdvancements;
		private final Set<Listener<DetailedCrafterTrigger.Instance>> listeners = Sets.<ICriterionTrigger.Listener<DetailedCrafterTrigger.Instance>>newHashSet();

		public Listeners(PlayerAdvancements playerAdvancementsIn) {
			this.playerAdvancements = playerAdvancementsIn;
		}

		public boolean isEmpty()
		{
			return this.listeners.isEmpty();
		}

		public void add(ICriterionTrigger.Listener<DetailedCrafterTrigger.Instance> listener) {
			this.listeners.add(listener);
		}
		public void remove(ICriterionTrigger.Listener<DetailedCrafterTrigger.Instance> listener) {
			this.listeners.remove(listener);
		}
		public void trigger(NBTTagCompound tag) {
			List<Listener<DetailedCrafterTrigger.Instance>> list = null;

			for (ICriterionTrigger.Listener<DetailedCrafterTrigger.Instance> listener : this.listeners) {
				if (((DetailedCrafterTrigger.Instance)listener.getCriterionInstance()).test(tag)) {
					if (list == null) {
						list = Lists.<ICriterionTrigger.Listener<DetailedCrafterTrigger.Instance>>newArrayList();
					}

					list.add(listener);
				}
			}

			if (list != null) {
				for (ICriterionTrigger.Listener<DetailedCrafterTrigger.Instance> listener1 : list) 	{
					listener1.grantCriterion(this.playerAdvancements);
				}
			}
		}
	}
}
