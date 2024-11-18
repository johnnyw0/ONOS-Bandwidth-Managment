package org.onosproject.slicemanagement;

import org.onlab.rest.BaseResource;
import org.onosproject.slicemanagement.SliceManager.Slice;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("slices")
public class SliceWebResource extends BaseResource {

    private final SliceManager sliceManager = get(SliceManager.class);

    @GET
    @Path("{sliceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSlice(@PathParam("sliceId") String sliceId) {
        Slice slice = sliceManager.getSlice(sliceId);
        if (slice == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(slice).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addOrUpdateSlice(Slice slice) {
        sliceManager.addOrUpdateSlice(slice.sliceId, slice.minrate, slice.maxrate, slice.flows);
        return Response.ok("Slice updated: " + slice.sliceId).build();
    }
}
