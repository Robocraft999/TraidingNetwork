package com.robocraft999.traidingnetwork.resourcepoints.nss;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.tags.ITag;
import net.minecraftforge.registries.tags.ITagManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class NSSItem implements NSSTag {

    private static final Set<NSSTag> createdTags = new HashSet<>();
    @Nullable
    private final CompoundTag nbt;

    @NotNull
    private final ResourceLocation resourceLocation;
    private final boolean isTag;

    protected NSSItem(@NotNull ResourceLocation resourceLocation, boolean isTag, @Nullable CompoundTag nbt) {
        this.resourceLocation = resourceLocation;
        this.isTag = isTag;
        if (isTag) {
            createdTags.add(this);
        }

        this.nbt = nbt != null && nbt.isEmpty() ? null : nbt;
    }

    /**
     * Helper method to create an {@link NSSItem} representing an item from an {@link ItemStack}
     */
    @NotNull
    public static NSSItem createItem(@NotNull ItemStack stack) {
        if (stack.isEmpty()) {
            throw new IllegalArgumentException("Can't make NSSItem with empty stack");
        }
        if (stack.isDamageableItem() && stack.hasTag()) {
            //If the stack is damageable check if the NBT is identical to what it would be without the damage attached
            // as creating a new ItemStack auto sets the NBT for the damage value, which we ideally do not want as it may
            // throw off various calculations
            if (stack.getOrCreateTag().equals(new ItemStack(stack.getItem()).getTag())) {
                //Skip including the NBT for the item as it auto gets added on stack creation anyways
                return createItem(stack.getItem(), null);
            }
        }
        return createItem(stack.getItem(), stack.getTag());
    }

    /**
     * Helper method to create an {@link NSSItem} representing an item from an {@link ItemLike}
     */
    @NotNull
    public static NSSItem createItem(@NotNull ItemLike itemProvider) {
        return createItem(itemProvider, null);
    }

    /**
     * Helper method to create an {@link NSSItem} representing an item from an {@link ItemLike} and an optional {@link CompoundTag}
     */
    @NotNull
    public static NSSItem createItem(@NotNull ItemLike itemProvider, @Nullable CompoundTag nbt) {
        Item item = itemProvider.asItem();
        if (item == Items.AIR) {
            throw new IllegalArgumentException("Can't make NSSItem with empty stack");
        }
        ResourceLocation registryName = ForgeRegistries.ITEMS.getKey(item);
        if (registryName == null) {
            throw new IllegalArgumentException("Can't make an NSSItem with an unregistered item");
        }
        //This should never be null, or it would have crashed on being registered
        return createItem(registryName, nbt);
    }

    /**
     * Helper method to create an {@link NSSItem} representing an item from a {@link ResourceLocation}
     */
    @NotNull
    public static NSSItem createItem(@NotNull ResourceLocation itemID) {
        return createItem(itemID, null);
    }

    /**
     * Helper method to create an {@link NSSItem} representing an item from a {@link ResourceLocation} and an optional {@link CompoundTag}
     */
    @NotNull
    public static NSSItem createItem(@NotNull ResourceLocation itemID, @Nullable CompoundTag nbt) {
        return new NSSItem(itemID, false, nbt);
    }

    /**
     * Helper method to create an {@link NSSItem} representing a tag from a {@link ResourceLocation}
     */
    @NotNull
    public static NSSItem createTag(@NotNull ResourceLocation tagId) {
        return new NSSItem(tagId, true, null);
    }

    /**
     * Helper method to create an {@link NSSItem} representing a tag from a {@link TagKey <Item>}
     */
    @NotNull
    public static NSSItem createTag(@NotNull TagKey<Item> tag) {
        return createTag(tag.location());
    }

    @Override
    public boolean representsTag() {
        return isTag;
    }

    @Override
    public void forEachElement(Consumer<NormalizedSimpleStack> consumer) {
        getTag().ifPresent(tag -> tag.map(t -> t.stream().map(Holder::value), ITag::stream)
                .map(createNew())
                .forEach(consumer)
        );
    }

    protected Function<Item, NormalizedSimpleStack> createNew() {
        return NSSItem::createItem;
    }

    @NotNull
    protected Optional<Either<Named<Item>, ITag<Item>>> getTag() {
        return getTag(ForgeRegistries.ITEMS);
    }

    /**
     * Helper to get the tag representation if this {@link NormalizedSimpleStack} is backed by a vanilla registry.
     */
    protected final Optional<Either<Named<Item>, ITag<Item>>> getTag(Registry<Item> registry) {
        if (representsTag()) {
            return registry.getTag(TagKey.create(registry.key(), getResourceLocation())).map(Either::left);
        }
        return Optional.empty();
    }

    /**
     * Helper to get the tag representation if this {@link NormalizedSimpleStack} is backed by a forge registry.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected final Optional<Either<Named<Item>, ITag<Item>>> getTag(IForgeRegistry<? extends Item> registry) {
        if (representsTag()) {
            ITagManager<? extends Item> tags = registry.tags();
            if (tags != null) {
                return Optional.of(Either.right(tags.getTag((TagKey) tags.createTagKey(getResourceLocation()))));
            }
        }
        return Optional.empty();
    }

    /**
     * @return A set of all the {@link NSSTag}s that have been created that represent a {@link ITag}
     *
     * @apiNote This method is meant for internal use of adding Tag -> Type and Type -> Tag conversions
     */
    public static Set<NSSTag> getAllCreatedTags() {
        return ImmutableSet.copyOf(createdTags);
    }

    /**
     * Clears the cache of what {@link AbstractNSSTag}s have been created that represent {@link ITag}s
     *
     * @apiNote This method is meant for internal use when the EMC mapper is reloading.
     */
    public static void clearCreatedTags() {
        createdTags.clear();
    }

    @Override
    public String json() {
        String res = getJsonPrefix();
        if (representsTag()) {
            res += "#";
        }
        res += getResourceLocation();

        if (hasNBT()){
            return res + nbt;
        }
        return res;
    }

    public boolean hasNBT(){
        return getNBT() != null;
    }

    @Override
    public String toString() {
        String res = getType();
        if (representsTag()) {
            res += " Tag: " + getResourceLocation();
        } else {
            res += ": " + getResourceLocation();
        }

        if (hasNBT()) {
            return res + nbt;
        }
        return res;
    }

    @NotNull
    public String getType() {
        return "Item";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof NSSItem other && other instanceof NSSItem) {
            return representsTag() == other.representsTag() && getResourceLocation().equals(other.getResourceLocation()) && Objects.equals(nbt, ((NSSItem) o).nbt);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int code = representsTag() ? 31 + resourceLocation.hashCode() : resourceLocation.hashCode();
        if (hasNBT()) {
            code = 31 * code + getNBT().hashCode();
        }
        return code;
    }

    @NotNull
    public String getJsonPrefix() {
        //We prefer no prefix for NSSItem even though we do support ITEM|
        return "";
    }

    /**
     * @return The {@link ResourceLocation} representing the tag if this {@link NSSTag} represents a {@link ITag}, or the {@link ResourceLocation} of the object
     */
    @NotNull
    public ResourceLocation getResourceLocation() {
        return resourceLocation;
    }

    @Nullable
    public CompoundTag getNBT() {
        return nbt;
    }
}
