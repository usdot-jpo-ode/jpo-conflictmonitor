package us.dot.its.jpo.conflictmonitor.monitor.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.opengis.referencing.operation.MathTransform;

/**
 * Utility class to return a forward and reverse transform
 */
@Getter
@Setter
@AllArgsConstructor
public class MathTransformPair {
    MathTransform transform;
    MathTransform inverseTransform;
}
