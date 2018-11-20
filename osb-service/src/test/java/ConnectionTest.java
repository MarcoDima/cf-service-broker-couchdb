import de.evoila.Application;
import de.evoila.cf.broker.InstanceParamTest;
import de.evoila.cf.broker.model.ServiceInstanceRequest;
import de.evoila.cf.broker.model.catalog.ServiceDefinition;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.service.impl.CatalogServiceImpl;
import de.evoila.cf.broker.service.impl.DeploymentServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Marco Di Martino
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ContextConfiguration
public class ConnectionTest{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DeploymentServiceImpl deploymentService;

    @Autowired
    private CatalogServiceImpl catalogService;


    @Test
    public void testAgainstCatalog () throws Exception {
        this.mockMvc.perform(get("/v2/catalog")
                .header("X-Broker-API-Version", "2.13")
                .with(httpBasic("admin", "cloudfoundry")))
                .andDo(print()).andExpect(status().isOk());
    }

    /*@Test
    public void testParametersValidation () throws Exception {
        RandomString randomString = new RandomString(15);
        String instanceId = randomString.nextString().toLowerCase();
        JsonNode schema = JsonLoader.fromPath("./src/test/resources/valid_params.json");
        this.mockMvc.perform(put("/v2/service_instances/"+instanceId+"?accepts_incomplete=true")
                .header("X-Broker-API-Version", "2.13")
                .contentType(MediaType.APPLICATION_JSON)
                .content(schema.toString())
                .with(httpBasic("admin", "cloudfoundry")))
                .andDo(print()).andExpect(status().isAccepted());
    }
    */

    @Test
    public void testValidateParametersOnCreateInstance () throws Exception {

        List<ServiceDefinition> services = catalogService.getCatalog().getServices();
        List<Plan> plans = services.get(0).getPlans();
        Plan plan = plans.get(0);

        ServiceInstanceRequest serviceInstanceRequest = new ServiceInstanceRequest(services.get(0).getId(), plan.getId(), "org-guid-here", "space-guid-here", null);

        Map<String, Object> params = new HashMap<>();
        params.put("name", "thisValueIsTooLong");

        serviceInstanceRequest.setParameters(params);

        InstanceParamTest paramTest = new InstanceParamTest();
        paramTest.testCreateInstanceParameterValidationFailsOnValues(serviceInstanceRequest, plan);

        params.remove("name");
        params.put("thisKeyIsNotValid", "value");

        paramTest.testCreateInstanceParameterValidationFailsOnKeys(serviceInstanceRequest, plan);

        params.remove("thisKeyIsNotValid");
        params.put("name", "mDima");

        paramTest.testCreateInstanceParameterValidationSuccess(serviceInstanceRequest, plan);

    }
}
