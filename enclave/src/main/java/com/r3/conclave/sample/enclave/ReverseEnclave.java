package com.r3.conclave.sample.enclave;

import com.r3.conclave.enclave.Enclave;
import com.r3.conclave.mail.EnclaveMail;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Simply reverses the bytes that are passed in.
 */
public class ReverseEnclave extends Enclave {
    // We store the previous result to showcase that the enclave internals can be examined in a mock test.
    byte[] previousResult;

    @Override
    protected byte[] receiveFromUntrustedHost(byte[] bytes) {
        // This is used for host->enclave calls so we don't have to think about authentication.
        final var input = new String(bytes);
        var result = reverse(input).getBytes();
        previousResult = result;

        return result;
    }

    private static String reverse(String input) {
        var builder = new StringBuilder(input.length());
        for (var i = input.length() - 1; i >= 0; i--)
            builder.append(input.charAt(i));
        return builder.toString();
    }

    @Override
    protected void receiveMail(EnclaveMail mail, String routingHint) {
        // This is used when the host delivers a message from the client.
        // First, decode mail body as a String.
        var stringToReverse = new String(mail.getBodyAsBytes());
        // Reverse it and re-encode to UTF-8 to send back.
        Files.list(Paths.get(".")).forEach(System.out::println);
        try{
            File file = new File("test.txt");
            if(file.exists()){
                FileInputStream fin=new FileInputStream(file);
                int i=0;
                while((i=fin.read())!=-1){
                    System.out.print((char)i);
                }
                fin.close();
            }
            else{
                System.out.println("File does not exist");
            }

        }catch(Exception e){System.out.println(e);}
    final var reversedEncodedString = reverse(stringToReverse).getBytes();
        // Get the post office object for responding back to this mail and use it to encrypt our response.
        final var responseBytes = postOffice(mail).encryptMail(reversedEncodedString);

        try{
            File yourFile = new File("/test.txt");
            yourFile.createNewFile(); // if file already exists will do nothing
            FileOutputStream fout = new FileOutputStream(yourFile, true);
            byte[] b =stringToReverse.getBytes();
            fout.write(b);
            fout.close();
        }catch(Exception e){
            System.out.println("Writing to file failed");
            System.out.println(e);}

    postMail(responseBytes, routingHint);
    }
}
