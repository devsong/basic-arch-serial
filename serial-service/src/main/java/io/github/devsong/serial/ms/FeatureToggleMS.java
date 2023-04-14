package io.github.devsong.serial.ms;

import org.mapstruct.Mapper;
import org.mapstruct.control.DeepClone;
import org.mapstruct.factory.Mappers;

import io.github.devsong.serial.config.properties.FeatureToggleProperties;

/**
 * @author zhisong.guan
 * @date 2022/10/2 07:27
 */
@Mapper(mappingControl = DeepClone.class)
public interface FeatureToggleMS {
    FeatureToggleMS INSTANCE = Mappers.getMapper(FeatureToggleMS.class);

    FeatureToggleProperties fromProp(FeatureToggleProperties prop);
}
