package se.vidstige.jadb.managers;

import se.vidstige.jadb.JadbDevice;
import se.vidstige.jadb.JadbException;
import se.vidstige.jadb.RemoteFile;
import se.vidstige.jadb.Stream;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Java interface to package manager. Launches package manager through jadb
 */
public class PackageManager {
    private final JadbDevice device;

    public PackageManager(JadbDevice device) {
        this.device = device;
    }

    private String getErrorMessage(String target, String errorMessage) {
        return "Could not " + "install" + " " + target + ": " + errorMessage;
    }

    private void verifyOperation(String target, String result) throws JadbException {
        if (!result.contains("Success")) throw new JadbException(getErrorMessage(target, result));
    }

    private void remove(RemoteFile file) throws IOException, JadbException {
        InputStream s = device.executeShell("rm", "-f", file.getPath());
        Stream.readAll(s, StandardCharsets.UTF_8);
    }

    private void install(File apkFile, List<String> extraArguments) throws IOException, JadbException {
        RemoteFile remote = new RemoteFile("/data/local/tmp/" + apkFile.getName());
        device.push(apkFile, remote);
        List<String> arguments = new ArrayList<>();
        arguments.add("install");
        arguments.addAll(extraArguments);
        arguments.add(remote.getPath());
        InputStream s = device.executeShell("pm", arguments.toArray(new String[0]));
        String result = Stream.readAll(s, StandardCharsets.UTF_8);
        remove(remote);
        verifyOperation(apkFile.getName(), result);
    }

    public void install(File apkFile) throws IOException, JadbException {
        install(apkFile, new ArrayList<String>(0));
    }

    public void installWithOptions(File apkFile, List<? extends InstallOption> options) throws IOException, JadbException {
        List<String> optionsAsStr = new ArrayList<>(options.size());

        for(InstallOption installOption : options) {
            optionsAsStr.add(installOption.getStringRepresentation());
        }
        install(apkFile, optionsAsStr);
    }

    public void forceInstall(File apkFile) throws IOException, JadbException {
        installWithOptions(apkFile, Collections.singletonList(REINSTALL_KEEPING_DATA));
    }

    //<editor-fold desc="InstallOption">
    public static class InstallOption {
        private final StringBuilder stringBuilder = new StringBuilder();

        InstallOption(String ... varargs) {
            String suffix = "";
            for(String str: varargs) {
                stringBuilder.append(suffix).append(str);
                suffix = " ";
            }
        }

        private String getStringRepresentation() {
            return stringBuilder.toString();
        }
    }

    public static final InstallOption REINSTALL_KEEPING_DATA =
            new InstallOption("-r");

    //</editor-fold>
}
