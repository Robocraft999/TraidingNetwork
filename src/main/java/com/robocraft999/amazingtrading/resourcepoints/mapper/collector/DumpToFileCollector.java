package com.robocraft999.amazingtrading.resourcepoints.mapper.collector;

import com.robocraft999.amazingtrading.AmazingTrading;
import com.robocraft999.amazingtrading.resourcepoints.conversion.ConversionGroup;
import com.robocraft999.amazingtrading.resourcepoints.conversion.CustomConversion;
import com.robocraft999.amazingtrading.resourcepoints.conversion.CustomConversionFile;
import com.robocraft999.amazingtrading.resourcepoints.mapper.arithmetic.IValueArithmetic;
import com.robocraft999.amazingtrading.resourcepoints.nss.NormalizedSimpleStack;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class DumpToFileCollector<A extends IValueArithmetic<?>> extends AbstractMappingCollector<NormalizedSimpleStack, Long, A> {

    public static String currentGroupName = "default";
    private final CustomConversionFile out = new CustomConversionFile();
    private final IExtendedMappingCollector<NormalizedSimpleStack, Long, A> inner;
    private final File file;

    public DumpToFileCollector(File f, IExtendedMappingCollector<NormalizedSimpleStack, Long, A> inner) {
        super(inner.getArithmetic());
        this.file = f;
        this.inner = inner;
    }

    @Override
    public void setValueFromConversion(int outnumber, NormalizedSimpleStack something, Map<NormalizedSimpleStack, Integer> ingredientsWithAmount) {
        inner.setValueFromConversion(outnumber, something, ingredientsWithAmount);
        if (something == null || ingredientsWithAmount.containsKey(null)) {
            return;
        }
        out.values.conversion.add(CustomConversion.getFor(outnumber, something, ingredientsWithAmount));
    }

    @Override
    public void addConversion(int outnumber, NormalizedSimpleStack output, Map<NormalizedSimpleStack, Integer> ingredientsWithAmount, A arithmeticForConversion) {
        inner.addConversion(outnumber, output, ingredientsWithAmount, arithmeticForConversion);
        if (output == null || ingredientsWithAmount.containsKey(null)) {
            return;
        }
        if (!out.groups.containsKey(currentGroupName)) {
            out.groups.put(currentGroupName, new ConversionGroup());
        }
        ConversionGroup group = out.groups.get(currentGroupName);
        group.conversions.add(CustomConversion.getFor(outnumber, output, ingredientsWithAmount));
    }

    @Override
    public void setValueBefore(NormalizedSimpleStack something, Long value) {
        inner.setValueBefore(something, value);
        if (something == null) {
            return;
        }
        out.values.setValueBefore.put(something, value);
    }

    @Override
    public void setValueAfter(NormalizedSimpleStack something, Long value) {
        inner.setValueAfter(something, value);
        if (something == null) {
            return;
        }
        out.values.setValueAfter.put(something, value);
    }

    @Override
    public void finishCollection() {
        try {
            out.write(file);
        } catch (IOException e) {
            AmazingTrading.LOGGER.error("Failed to dump to file", e);
        }
        inner.finishCollection();
    }
}