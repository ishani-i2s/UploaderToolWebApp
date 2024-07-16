package com.example.demo.service.Impl;
import com.example.demo.controller.ExcelHelper;
import com.example.demo.entity.TaskDetails;
import com.example.demo.service.RouteChangeService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

@Service
public class routeChangesImpl implements RouteChangeService {
    String baseURL="https://ifscloud.tsunamit.com";
    StringBuilder stringBuilder=new StringBuilder(baseURL);

    @Autowired
    private RestTemplate restTemplate;

    public RestTemplate restTemplate() {
      CloseableHttpClient httpClient = HttpClients.createDefault();
      HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
      RestTemplate restTemplatePatch = new RestTemplate(factory);
      return restTemplatePatch;
    }


//    @Autowired
//    private RestTemplate restPatchTemplate;


    @Override
    public List<TaskDetails> save(MultipartFile file, String accessToken) {
        List<TaskDetails> errorList = new ArrayList<>();
        try {
            System.out.println("Route changes save method");
            List<TaskDetails> taskDetailsList = ExcelHelper.readTaskDetails(file.getInputStream());
            System.out.println("task details count: " + taskDetailsList.size());

            //get the required fields
            errorList = validateObjId(taskDetailsList,accessToken);
            taskDetailsList.removeAll(errorList);

            errorList.addAll(validateObjIdRowKey(taskDetailsList,accessToken));
            taskDetailsList.removeAll(errorList);

            errorList.addAll(validatePlannedStartDate(taskDetailsList));
            taskDetailsList.removeAll(errorList);

            System.out.println("task details count after validation: " + taskDetailsList.size());

            if(taskDetailsList.size()==0){
                return errorList;
            }else{
                System.out.println("inside else");
                List<TaskDetails> postStatus= updateTaskDetails(taskDetailsList,accessToken);
                System.out.println("The post status is"+postStatus);
                if(postStatus.size()>0){
                    errorList.addAll(postStatus);
                    return errorList;
                }else {
                    return errorList;
                }
            }
        }catch(Exception e){
            return errorList;
        }
    }

    @Override
    public List<String> getSite(String accessToken) {
        HttpEntity<Void> httpEntity= new HttpEntity<>(gethttpHeaders(accessToken));
        stringBuilder.setLength(0);
        String url=stringBuilder.append(baseURL).append("/main/ifsapplications/projection/v1/WorkTaskHandling.svc/Reference_UserAllowedSiteLov").toString();
        System.out.println("The url is"+ url);
        try {
            ResponseEntity<Map> responseEntity=restTemplate.exchange(url, HttpMethod.GET, httpEntity, Map.class);
            Map<String, Object> responseMap = responseEntity.getBody();
            System.out.println(responseMap);
            List<Map<String, Object>> responseList = (List<Map<String, Object>>) responseMap.get("value");
            List<String> siteList = new ArrayList<>();
            for (Map<String, Object> map : responseList) {
                siteList.add(map.get("Contract").toString()+"-"+map.get("ContractDesc").toString());
            }
            System.out.println("The site list is"+siteList);
            return siteList;
        }catch (Exception e){
            System.out.println("Failed to get site list");
            return null;
        }
    }

    @Override
    public List<TaskDetails> getTaskDetails(String accessToken, String site, String status, String plannedStart) {
        HttpEntity<Void> httpEntity= new HttpEntity<>(gethttpHeaders(accessToken));
        stringBuilder.setLength(0);

        //convert the status into uppercase
        status=status.toUpperCase();

        //convert the planned start into date format
        String todayT00= new StringBuilder().append(plannedStart).append("T00:00:00Z").toString();
        String todayT23= new StringBuilder().append(plannedStart).append("T23:59:59Z").toString();

        String url = stringBuilder.append(String.format("https://ifscloud.tsunamit.com/main/ifsapplications/projection/v1/WorkTaskHandling.svc/JtTaskSet?$filter=(((Objstate eq IfsApp.WorkTaskHandling.JtTaskState'%s') and Site eq '%s') and PlannedStart ge %s and PlannedStart le %s)", status, site,todayT00, todayT23)).toString();
//        String url=stringBuilder.append(baseURL).append("/main/ifsapplications/projection/v1/WorkTaskHandling.svc/JtTaskSet?$filter=(((Objstate eq IfsApp.WorkTaskHandling.JtTaskState'").append(status).append("') and Site eq '").append(site).append("') and PlannedStart ge ").append(todayT00).append(" and PlannedStart le ").append(todayT23).append(")").toString();
        System.out.println("The url is "+ url);
        try {
            ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, Map.class);
            Map<String, Object> responseMap = responseEntity.getBody();
            System.out.println(responseMap);
            List<Map<String, Object>> responseList = (List<Map<String, Object>>) responseMap.get("value");
            List<TaskDetails> taskDetailsList = new ArrayList<>();
            responseList.forEach(map -> {
                TaskDetails taskDetails = new TaskDetails();
                System.out.println("task id is"+map.get("TaskSeq"));
                System.out.println("description is"+map.get("Description"));
                System.out.println("site is"+map.get("Site"));
                System.out.println("planned start is"+map.get("PlannedStart"));
                System.out.println("status is"+map.get("Objstate"));
                System.out.println("object id is"+map.get("ActualObjectId"));
                System.out.println("work type is"+map.get("WorkTypeId"));
                taskDetails.setTaskId(map.get("TaskSeq").toString());
                taskDetails.setDescription((String)map.get("Description"));
                taskDetails.setSite((String)map.get("Site"));
                taskDetails.setPlannedStartDate((String)map.get("PlannedStart"));
                taskDetails.setStatus((String)map.get("Objstate"));
                taskDetails.setObjectId((String)map.get("ActualObjectId"));
                taskDetails.setWorkType((String)map.get("WorkTypeId"));
                taskDetailsList.add(taskDetails);
            });
            System.out.println("The task details list is"+taskDetailsList);
            return taskDetailsList;
        }catch (Exception e){
            System.out.println("Failed to get task details");
            return null;
        }
    }

    private List<TaskDetails> validateObjId(List<TaskDetails> taskDetailsList, String accessToken) {
        System.out.println("inside object Id validation");
        HttpEntity<Void> httpEntity = new HttpEntity<>(gethttpHeaders(accessToken));
        List<TaskDetails> invalidList = new ArrayList<>();
        for (TaskDetails taskDetails : taskDetailsList) {
            stringBuilder.setLength(0);
            stringBuilder.append(baseURL);
            String url = stringBuilder.append(String.format("/main/ifsapplications/projection/v1/WorkTaskHandling.svc/MaintenanceObjectLovFunction(ConnectionType=IfsApp.WorkTaskHandling.MaintConnectionType'Equipment')?$filter=(Contract eq '%s')", taskDetails.getSite())).toString();
            System.out.println("URL: " + url);
            try{
                ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, Map.class);
                Map<String, Object> responseMap = responseEntity.getBody();
                System.out.println("The response map is" + responseMap);
                List<Map<String, Object>> responseList = (List<Map<String, Object>>) responseMap.get("value");
                List<String> objList = new ArrayList<>();
                for (Map<String, Object> map : responseList) {
                    objList.add((String) map.get("MchCode"));
                }
                System.out.println("Object List : " + objList);
                System.out.println("Object Id : " + taskDetails.getObjectId());
                if (!objList.contains(taskDetails.getObjectId())) {
                    System.out.println("invalid object Id");
                    taskDetails.setLog("Invalid actual object Id");
                    invalidList.add(taskDetails);
                }
                return invalidList;
            } catch (Exception e){
                taskDetails.setLog("check object Id API call failed");
                invalidList.add(taskDetails);
                return invalidList;
            }
        }
        return invalidList;
    }

    private List<TaskDetails> validateObjIdRowKey(List<TaskDetails> taskDetailsList, String accessToken) {
        System.out.println("inside object Id RowKey validation");
        HttpEntity<Void> httpEntity = new HttpEntity<>(gethttpHeaders(accessToken));
        List<TaskDetails> invalidList = new ArrayList<>();
        for (TaskDetails taskDetails : taskDetailsList) {
            stringBuilder.setLength(0);
            stringBuilder.append(baseURL);
            String url = stringBuilder.append(String.format("/main/ifsapplications/projection/v1/WorkTaskHandling.svc/GetRowkeyByObject(ConnectionType=IfsApp.WorkTaskHandling.MaintConnectionType'Equipment',ObjectSite='%s',ObjectId='%s')", taskDetails.getSite(), taskDetails.getObjectId())).toString();
            System.out.println("URL: " + url);
            try {
                ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, Map.class);
                Map<String, Object> responseMap = responseEntity.getBody();
                System.out.println("The response map is " + responseMap);

                if (responseMap != null && responseMap.containsKey("ObjectRowkey")) {
                    String objectRowkey = (String) responseMap.get("ObjectRowkey");
                    if ("~INVALID OBJECT~".equals(objectRowkey)) {
                        taskDetails.setLog("Invalid actual object Id");
                        invalidList.add(taskDetails);
                    } else {
                        taskDetails.setObjectIdRowKey(objectRowkey);
                    }
                } else {
                    taskDetails.setLog("Invalid response structure");
                    invalidList.add(taskDetails);
                }
            } catch (Exception e) {
                taskDetails.setLog("Check object Id API call failed");
                invalidList.add(taskDetails);
            }
        }
        return invalidList;
    }

    private List<TaskDetails> validatePlannedStartDate(List<TaskDetails> taskDetailsList) {
        List<TaskDetails> invalidList = new ArrayList<>();
        for (TaskDetails taskDetails : taskDetailsList) {
            // Validate PlannedStart date format
            if (!isValidDate(taskDetails.getPlannedStartDate())) {
                taskDetails.setLog("Invalid Planned Start date format");
                invalidList.add(taskDetails);
            }
        }
        return invalidList;
    }

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private boolean isValidDate(String dateStr) {
        try {
            LocalDateTime.parse(dateStr, DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    @Override
    public List<TaskDetails> updateTaskDetails(List<TaskDetails> taskDetails, String accessToken) {
        System.out.println("Inside the patch task details method");
//        System.out.println("The functional object list is"+funList);
        List<TaskDetails> invalidList= new ArrayList<>();
        HttpHeaders headers= gethttpHeaders(accessToken);

        for(TaskDetails task : taskDetails ){
            stringBuilder.setLength(0);
            stringBuilder.append(baseURL);
            String url = stringBuilder.append(String.format("/main/ifsapplications/projection/v1/WorkTaskHandling.svc/JtTaskSet(TaskSeq=%s)", task.getTaskId())).toString();
            System.out.println("URL update task details : " + url);

            Map<String, Object> payload = new java.util.HashMap<>();
            payload.put("PlannedStart", task.getPlannedStartDate());
            payload.put("ActualObjectId", task.getObjectId());
            payload.put("ActualObjConnRowkey", task.getObjectIdRowKey());

            HttpEntity<Map> httpEntity = new HttpEntity<>(payload, headers);
            System.out.println("The payload is"+payload);
            System.out.println("HttpEntity is"+httpEntity);
            try{
                var response= restTemplate().exchange(url, HttpMethod.PATCH,  httpEntity, Void.class);
                System.out.println("status code : " + response.getStatusCode());
                if(response.getStatusCode().toString().equals("200 OK")){
                    System.out.println("success");
                    task.setLog("Successfull");
                    invalidList.add(task);
                }else {
                    System.out.println("patch update failed");
                    task.setLog("update failed");
                    invalidList.add(task);
                }
            }catch (Exception e) {
                System.out.println("inside catch");
                e.printStackTrace();
                String errorResponse = e.getMessage();
                String errorMessage = extractErrorMessageFromJson(errorResponse);
                System.out.println("error response : " + errorResponse);
                System.out.println("error message : " + errorMessage);
                task.setLog(errorMessage);
                invalidList.add(task);
            }
        }

        System.out.println("The invalid list is"+invalidList);
        System.out.println("Posted all the functional objects");
        return invalidList;
    }

    private HttpHeaders gethttpHeaders(String accessToken) {
        HttpHeaders headers= new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);
        return headers;
    }

    private String extractErrorMessageFromJson(String errorResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(errorResponse.substring(errorResponse.indexOf("{")));
            System.out.println("The root node is"+rootNode);
            JsonNode errorMessageNode = rootNode.path("error").path("message");
            return errorMessageNode.asText();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error occurred while parsing JSON response.";
        }
    }
}

