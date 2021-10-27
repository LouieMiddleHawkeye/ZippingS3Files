package com.hawkeyeinnovations.zippings3files;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.InstanceType;
import software.amazon.awssdk.services.ec2.model.RunInstancesRequest;
import software.amazon.awssdk.services.ec2.model.RunInstancesResponse;
import software.amazon.awssdk.services.ec2.model.StartInstancesRequest;
import software.amazon.awssdk.services.ec2.model.StopInstancesRequest;

public class CreateEC2Instance {

    public static void main(String[] args) {
        String name = "ec2-ew1-dev-dynamicZip";
        String amiId = "ami-05cd35b907b4ffe77";
        Region region = Region.EU_WEST_1;

        Ec2Client ec2 = Ec2Client.builder()
            .region(region)
            .build();

        String instanceId = createEC2Instance(ec2, name, amiId) ;
        System.out.println("The Amazon EC2 Instance ID is " + instanceId);

        startInstance(ec2, instanceId);

        ec2.close();
    }

    public static void startInstance(Ec2Client ec2, String instanceId) {
        StartInstancesRequest request = StartInstancesRequest.builder()
            .instanceIds(instanceId)
            .build();

        ec2.startInstances(request);
        System.out.println("Successfully started instance %s" + instanceId);
    }

    public static void stopInstance(Ec2Client ec2, String instanceId) {
        StopInstancesRequest request = StopInstancesRequest.builder()
            .instanceIds(instanceId)
            .build();

        ec2.stopInstances(request);
        System.out.println("Successfully stopped instance %s" + instanceId);
    }

    public static String createEC2Instance(Ec2Client ec2, String name, String amiId ) {
        RunInstancesRequest runRequest = RunInstancesRequest.builder()
            .imageId(amiId)
            .instanceType(InstanceType.T2_MICRO)
            .maxCount(1)
            .minCount(1)
            .build();

        RunInstancesResponse response = ec2.runInstances(runRequest);
        String instanceId = response.instances().get(0).instanceId();

        System.out.println("Successfully started EC2 Instance %s based on AMI %s" + instanceId + amiId);
        return instanceId;
    }
}
