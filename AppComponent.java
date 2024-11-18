package org.onosproject.slicemanagement;

import org.onosproject.core.CoreService;
import org.onosproject.rest.AbstractWebApplication;
import org.osgi.service.component.annotations.*;

@Component(immediate = true)
public class AppComponent {

    @Reference
    private CoreService coreService;

    private static final String APP_NAME = "org.onosproject.slicemanagement";

    @Activate
    protected void activate() {
        coreService.registerApplication(APP_NAME);
        System.out.println("Slice Management Application Started");
    }

    @Deactivate
    protected void deactivate() {
        System.out.println("Slice Management Application Stopped");
    }
}
