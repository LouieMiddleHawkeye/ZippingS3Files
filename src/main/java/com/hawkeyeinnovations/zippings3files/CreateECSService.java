package com.hawkeyeinnovations.zippings3files;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.*;

public class CreateECSService {

    public static void main(String[] args) {
        String clusterName = "dynamic-zipping-cluster";
//        String serviceName = "dynamic-zipping-service";
//        String securityGroup = "dynamic-zipping-security-group";
//        String subnet = "dynamic-zipping-subnet";
//        String taskDefinition = "dynamic-zipping-task-def";
        Region region = Region.EU_WEST_1;

        EcsClient ecsClient = EcsClient.builder()
            .region(region)
            .build();

        String clusterArn = createECSCluster(ecsClient, clusterName);
        System.out.println("The cluster ARN is " + clusterArn);

//        String serviceArn = createECSService(ecsClient, clusterName, serviceName, securityGroup, subnet, taskDefinition);
//        System.out.println("The service ARN is " + serviceArn);

        ecsClient.close();
    }

    public static String createECSCluster(EcsClient ecsClient, String clusterName) {
        try {
            ExecuteCommandConfiguration commandConfiguration =  ExecuteCommandConfiguration.builder()
                .logging(ExecuteCommandLogging.DEFAULT)
                .build();

            ClusterConfiguration clusterConfiguration = ClusterConfiguration.builder()
                .executeCommandConfiguration(commandConfiguration)
                .build();

            CreateClusterRequest clusterRequest = CreateClusterRequest.builder()
                .clusterName(clusterName)
                .configuration(clusterConfiguration)
                .build();

            CreateClusterResponse response = ecsClient.createCluster(clusterRequest) ;
            return response.cluster().clusterArn();
        } catch (EcsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }

    public static String createECSService(EcsClient ecsClient,
                                          String clusterName,
                                          String serviceName,
                                          String securityGroups,
                                          String subnets,
                                          String taskDefinition) {
        try {
            AwsVpcConfiguration vpcConfiguration = AwsVpcConfiguration.builder()
                .securityGroups(securityGroups)
                .subnets(subnets)
                .build();

            NetworkConfiguration configuration = NetworkConfiguration.builder()
                .awsvpcConfiguration(vpcConfiguration)
                .build();

            CreateServiceRequest serviceRequest = CreateServiceRequest.builder()
                .cluster(clusterName)
                .networkConfiguration(configuration)
                .desiredCount(1)
                .launchType(LaunchType.FARGATE)
                .serviceName(serviceName)
                .taskDefinition(taskDefinition)
                .build();

            CreateServiceResponse response = ecsClient.createService(serviceRequest) ;
            return response.service().serviceArn();
        } catch (EcsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }
}
