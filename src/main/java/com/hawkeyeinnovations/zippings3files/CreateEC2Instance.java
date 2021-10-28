package com.hawkeyeinnovations.zippings3files;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import java.util.List;

public class CreateEC2Instance {

    public static void main(String[] args) {
        String amiId = "ami-05cd35b907b4ffe77";
        Region region = Region.EU_WEST_1;

        Ec2Client ec2 = Ec2Client.builder()
            .region(region)
            .build();

        // Create instance
        String instanceId = createEC2Instance(ec2, amiId);

        /*
        I assume we won't want (or need) to ssh onto this instance, but if we did this is how you can create a ssh key
        and security. I think the Jenkins key could be used to ssh if needed?
         */

//        // Create ssh key
//        String keyName = "dynamicZipKeyHawkeye";
//        createEC2KeyPair(ec2, keyName);

//        // Create security group
//        String securityGroupName = "dynamicZipSecurityGroup";
//        String securityGroupDescription = "Dynamic zip security group";
//        // This is the default vpc for fdp-dev account
//        String vpcId = "vpc-cf5b85b6";
//        String securityGroupId = createEC2SecurityGroup(ec2, securityGroupName, securityGroupDescription, vpcId);

        // Start instance
        startInstance(ec2, instanceId);

        // Do some zipping

        // Cleanup
//        stopInstance(ec2, instanceId);
//        deleteEC2KeyPair(ec2, keyName);
//        deleteEC2SecurityGroup(ec2, securityGroupId);
//        terminateEC2(ec2, instanceId);

        ec2.close();
    }

    public static String createEC2Instance(Ec2Client ec2, String amiId ) {
        RunInstancesRequest runRequest = RunInstancesRequest.builder()
            .imageId(amiId)
            .instanceType(InstanceType.T2_MICRO)
            .maxCount(1)
            .minCount(1)
            .build();

        RunInstancesResponse response = ec2.runInstances(runRequest);
        String instanceId = response.instances().get(0).instanceId();

        System.out.println("Successfully created EC2 Instance %s based on AMI " + instanceId + amiId);
        return instanceId;
    }

    public static void terminateEC2(Ec2Client ec2, String instanceID) {
        try{
            TerminateInstancesRequest ti = TerminateInstancesRequest.builder()
                .instanceIds(instanceID)
                .build();

            TerminateInstancesResponse response = ec2.terminateInstances(ti);
            List<InstanceStateChange> list = response.terminatingInstances();

            for (InstanceStateChange sc : list) {
                System.out.println("The ID of the terminated instance is " + sc.instanceId());
            }
        } catch (Ec2Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public static void startInstance(Ec2Client ec2, String instanceId) {
        StartInstancesRequest request = StartInstancesRequest.builder()
            .instanceIds(instanceId)
            .build();

        ec2.startInstances(request);
        System.out.println("Successfully started instance " + instanceId);
    }

    public static void stopInstance(Ec2Client ec2, String instanceId) {
        StopInstancesRequest request = StopInstancesRequest.builder()
            .instanceIds(instanceId)
            .build();

        ec2.stopInstances(request);
        System.out.println("Successfully stopped instance " + instanceId);
    }

    public static void createEC2KeyPair(Ec2Client ec2, String keyName) {
        try {
            CreateKeyPairRequest request = CreateKeyPairRequest.builder()
                .keyName(keyName).build();

            CreateKeyPairResponse createKeyPairResponse = ec2.createKeyPair(request);
            // This is where the key is stored
            // createKeyPairResponse.keyMaterial();
            System.out.println(
                "Successfully created key pair named " +
                keyName);
        } catch (Ec2Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public static void deleteEC2KeyPair(Ec2Client ec2, String keyName) {
        try {
            DeleteKeyPairRequest request = DeleteKeyPairRequest.builder()
                .keyName(keyName)
                .build();

            DeleteKeyPairResponse response = ec2.deleteKeyPair(request);
            System.out.println(
                "Successfully deleted key pair named " + keyName);
        } catch (Ec2Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public static String createEC2SecurityGroup(Ec2Client ec2, String groupName, String groupDesc, String vpcId) {
        try {
            // Create security group
            CreateSecurityGroupRequest createRequest = CreateSecurityGroupRequest.builder()
                .groupName(groupName)
                .description(groupDesc)
                .vpcId(vpcId)
                .build();

            CreateSecurityGroupResponse resp= ec2.createSecurityGroup(createRequest);

            // Configure security group
            IpRange ipRange = IpRange.builder()
                .cidrIp("0.0.0.0/0").build();

//            IpPermission ipPerm = IpPermission.builder()
//                .ipProtocol("tcp")
//                .toPort(80)
//                .fromPort(80)
//                .ipRanges(ipRange)
//                .build();

            IpPermission ipPerm2 = IpPermission.builder()
                .ipProtocol("tcp")
                .toPort(22)
                .fromPort(22)
                .ipRanges(ipRange)
                .build();

            AuthorizeSecurityGroupIngressRequest authRequest =
                AuthorizeSecurityGroupIngressRequest.builder()
                    .groupName(groupName)
                    .ipPermissions(ipPerm2)
                    .build();

            AuthorizeSecurityGroupIngressResponse authResponse =
                ec2.authorizeSecurityGroupIngress(authRequest);

            System.out.println(
                "Successfully added ingress policy to Security Group " +
                groupName);

            return resp.groupId();
        } catch (Ec2Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }

    public static void deleteEC2SecurityGroup(Ec2Client ec2, String groupId) {
        try {
            DeleteSecurityGroupRequest request = DeleteSecurityGroupRequest.builder()
                .groupId(groupId)
                .build();

            ec2.deleteSecurityGroup(request);
            System.out.println(
                "Successfully deleted Security Group with id " + groupId);
        } catch (Ec2Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
