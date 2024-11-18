package org.onosproject.slicemanagement;

import org.onosproject.net.*;
import org.onosproject.net.flow.*;
import org.onosproject.net.flow.criteria.Criteria;
import org.onosproject.net.flow.instructions.Instructions;
import org.onosproject.net.flowobjective.FlowObjectiveService;
import org.onosproject.net.flowobjective.ForwardingObjective;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

import java.util.HashMap;
import java.util.Map;

@Component(immediate = true)
public class SliceManager {

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected FlowObjectiveService flowObjectiveService;

    private final Map<String, Slice> slices = new HashMap<>();

    public void addOrUpdateSlice(String sliceId, int minrate, int maxrate, String[] flows) {
        Slice slice = new Slice(sliceId, minrate, maxrate, flows);
        slices.put(sliceId, slice);
        installFlowRules(slice);
    }

    private void installFlowRules(Slice slice) {
        for (String flow : slice.flows) {
            String[] parts = flow.split("->");
            if (parts.length != 2) continue;

            String srcIp = parts[0].trim();
            String dstIp = parts[1].trim();

            TrafficSelector selector = DefaultTrafficSelector.builder()
                    .matchIPSrc(IpPrefix.valueOf(srcIp))
                    .matchIPDst(IpPrefix.valueOf(dstIp))
                    .build();

            TrafficTreatment treatment = DefaultTrafficTreatment.builder()
                    .add(Instructions.modL3Queue(slice.minrate, slice.maxrate))
                    .build();

            FlowRule flowRule = DefaultFlowRule.builder()
                    .forDevice(DeviceId.deviceId("device-id-here")) // Substitua pelo ID do dispositivo
                    .withSelector(selector)
                    .withTreatment(treatment)
                    .withPriority(50000)
                    .makePermanent()
                    .build();

            flowObjectiveService.forward(DeviceId.deviceId("device-id-here"),
                    ForwardingObjective.builder()
                            .withSelector(selector)
                            .withTreatment(treatment)
                            .withFlag(ForwardingObjective.Flag.VERSATILE)
                            .makePermanent()
                            .add());
        }
    }

    public Slice getSlice(String sliceId) {
        return slices.get(sliceId);
    }

    public static class Slice {
        public String sliceId;
        public int minrate;
        public int maxrate;
        public String[] flows;

        public Slice(String sliceId, int minrate, int maxrate, String[] flows) {
            this.sliceId = sliceId;
            this.minrate = minrate;
            this.maxrate = maxrate;
            this.flows = flows;
        }
    }
}
