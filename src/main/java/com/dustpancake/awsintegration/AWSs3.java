package com.dustpancake.awsintegration;

import com.amazonaws.auth.AWSCredentialsProvider;

import com.amazonaws.AmazonServiceException;

import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.AmazonS3;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import com.amazonaws.regions.Regions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class AWSs3 {
	final private AmazonS3 s3access;
	final private String bucketName = "brimage-bucket";

	public AWSs3(AWSCredentialsProvider awscred) {
		s3access = AmazonS3ClientBuilder
			.standard()
			.withCredentials(awscred)
			.withRegion(Regions.EU_WEST_1)
			.build();
	}

	public String getFile(String key) {
		try {
			S3Object o = s3access.getObject(bucketName, key);
			S3ObjectInputStream s3inp = o.getObjectContent();
			writeToFile(s3inp, key);
			s3inp.close();

		} catch(AmazonServiceException e) {
			// aws went wrong
			System.out.println(e);
			return "AWS S3 ERROR";

		} catch(IOException e) {
			// file reading went wrong
			System.out.println(e);
			return "FILE IO ERROR";

		} catch(Exception e) {
			// anything else
			System.out.println(e);
			return "UNKNOWN FILE ERROR";
		}

		return "";
	}

	public String writeToBucket(String fileName) {
		try {
			s3access.putObject(bucketName, fileName, new File(fileName));
		} catch(AmazonServiceException e) {
			// aws went wrong
			System.out.println(e);
			return "FILE ACCEPTED; UPLOAD ERROR";
		}
		return fileName;
	}

	private void writeToFile(S3ObjectInputStream s3inp, String fileName) throws IOException {
		FileOutputStream fos = new FileOutputStream(new File(fileName));
		byte[] buffer = new byte[1024];
		int i = 0;
		while((i = s3inp.read(buffer)) > 0) {
			fos.write(buffer, 0, i);
		}
		fos.close();
	}

}