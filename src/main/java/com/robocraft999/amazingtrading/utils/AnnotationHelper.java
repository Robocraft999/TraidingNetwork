package com.robocraft999.amazingtrading.utils;

import com.robocraft999.amazingtrading.TraidingNetwork;
import com.robocraft999.amazingtrading.api.mapper.RPMapper;
import com.robocraft999.amazingtrading.api.mapper.RecipeTypeMapper;
import com.robocraft999.amazingtrading.resourcepoints.mapper.IRPMapper;
import com.robocraft999.amazingtrading.resourcepoints.mapper.recipe.IRecipeTypeMapper;
import com.robocraft999.amazingtrading.resourcepoints.nss.NormalizedSimpleStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.forgespi.language.ModFileScanData.AnnotationData;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;

public class AnnotationHelper {

    private static final Type MAPPER_TYPE = Type.getType(RPMapper.class);
    private static final Type RECIPE_TYPE_MAPPER_TYPE = Type.getType(RecipeTypeMapper.class);
    public static List<IRecipeTypeMapper> getRecipeTypeMappers() {
        ModList modList = ModList.get();
        List<IRecipeTypeMapper> recipeTypeMappers = new ArrayList<>();
        Map<IRecipeTypeMapper, Integer> priorities = new HashMap<>();
        for (ModFileScanData scanData : modList.getAllScanData()) {
            for (AnnotationData data : scanData.getAnnotations()) {
                if (RECIPE_TYPE_MAPPER_TYPE.equals(data.annotationType()) && checkRequiredMods(data)) {
                    //If all the mods were loaded then attempt to get the processor
                    IRecipeTypeMapper mapper = getRecipeTypeMapper(data.memberName());
                    if (mapper != null) {
                        int priority = getPriority(data);
                        recipeTypeMappers.add(mapper);
                        priorities.put(mapper, priority);
                        TraidingNetwork.LOGGER.info("Found and loaded RecipeType Mapper: {}, with priority {}", mapper.getName(), priority);
                    }
                }
            }
        }
        recipeTypeMappers.sort(Collections.reverseOrder(Comparator.comparing(priorities::get)));
        return recipeTypeMappers;
    }

    //Note: We don't bother caching this value because RPMappingHandler#loadMappers caches our processed result
    public static List<IRPMapper<NormalizedSimpleStack, Long>> getRPMappers() {
        ModList modList = ModList.get();
        List<IRPMapper<NormalizedSimpleStack, Long>> rpMappers = new ArrayList<>();
        Map<IRPMapper<NormalizedSimpleStack, Long>, Integer> priorities = new HashMap<>();
        for (ModFileScanData scanData : modList.getAllScanData()) {
            for (AnnotationData data : scanData.getAnnotations()) {
                if (MAPPER_TYPE.equals(data.annotationType()) && checkRequiredMods(data)) {
                    //If all the mods were loaded then attempt to get the mapper
                    IRPMapper<?, ?> mapper = getRPMapper(data.memberName());
                    if (mapper != null) {
                        try {
                            IRPMapper<NormalizedSimpleStack, Long> rpMapper = (IRPMapper<NormalizedSimpleStack, Long>) mapper;
                            int priority = getPriority(data);
                            rpMappers.add(rpMapper);
                            priorities.put(rpMapper, priority);
                            TraidingNetwork.LOGGER.info("Found and loaded RP mapper: {}, with priority {}", mapper.getName(), priority);
                        } catch (ClassCastException e) {
                            TraidingNetwork.LOGGER.error("{}: Is not a mapper for {}, to {}", mapper.getClass(), NormalizedSimpleStack.class, Long.class, e);
                        }
                    }
                }
            }
        }
        rpMappers.sort(Collections.reverseOrder(Comparator.comparing(priorities::get)));
        return rpMappers;
    }

    @Nullable
    private static IRPMapper<?, ?> getRPMapper(String className) {
        return createOrGetInstance(className, IRPMapper.class, RPMapper.Instance.class, IRPMapper::getName);
    }

    @Nullable
    private static IRecipeTypeMapper getRecipeTypeMapper(String className) {
        return createOrGetInstance(className, IRecipeTypeMapper.class, RecipeTypeMapper.Instance.class, IRecipeTypeMapper::getName);
    }

    @Nullable
    private static <T> T createOrGetInstance(String className, Class<T> baseClass, Class<? extends Annotation> instanceAnnotation, Function<T, String> nameFunction) {
        //Try to create an instance of the class
        try {
            Class<? extends T> subClass = Class.forName(className).asSubclass(baseClass);
            //First try looking at the fields of the class to see if one of them is specified as the instance
            Field[] fields = subClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(instanceAnnotation)) {
                    if (Modifier.isStatic(field.getModifiers())) {
                        try {
                            Object fieldValue = field.get(null);
                            if (baseClass.isInstance(fieldValue)) {
                                T instance = (T) fieldValue;
                                TraidingNetwork.LOGGER.debug("Found specified {} instance for: {}. Using it rather than creating a new instance.", baseClass.getSimpleName(),
                                        nameFunction.apply(instance));
                                return instance;
                            } else {
                                TraidingNetwork.LOGGER.error("{} annotation found on non {} field: {}", instanceAnnotation.getSimpleName(), baseClass.getSimpleName(), field);
                                return null;
                            }
                        } catch (IllegalAccessException e) {
                            TraidingNetwork.LOGGER.error("{} annotation found on inaccessible field: {}", instanceAnnotation.getSimpleName(), field);
                            return null;
                        }
                    } else {
                        TraidingNetwork.LOGGER.error("{} annotation found on non static field: {}", instanceAnnotation.getSimpleName(), field);
                        return null;
                    }
                }
            }
            //If we don't have any fields that have the Instance annotation, then try to create a new instance of the class
            return subClass.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | LinkageError |
                 InvocationTargetException | NoSuchMethodException e) {
            TraidingNetwork.LOGGER.error("Failed to load: {}", className, e);
        }
        return null;
    }

    private static boolean checkRequiredMods(AnnotationData data) {
        Map<String, Object> annotationData = data.annotationData();
        if (annotationData.containsKey("requiredMods")) {
            //Check if all the mods the RPMapper wants to be loaded are loaded
            List<String> requiredMods = (List<String>) annotationData.get("requiredMods");
            if (requiredMods.stream().anyMatch(modid -> !ModList.get().isLoaded(modid))) {
                TraidingNetwork.LOGGER.debug("Skipped checking class {}, as its required mods ({}) are not loaded.", data.memberName(), Arrays.toString(requiredMods.toArray()));
                return false;
            }
        }
        return true;
    }

    private static int getPriority(AnnotationData data) {
        Map<String, Object> annotationData = data.annotationData();
        if (annotationData.containsKey("priority")) {
            return (int) annotationData.get("priority");
        }
        return 0;
    }
}
