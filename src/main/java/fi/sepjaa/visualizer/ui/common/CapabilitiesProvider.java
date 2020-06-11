package fi.sepjaa.visualizer.ui.common;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLCapabilitiesImmutable;
import com.jogamp.opengl.GLProfile;

public class CapabilitiesProvider {
	public static GLCapabilitiesImmutable instance() {
		GLCapabilities capabilities = new GLCapabilities(GLProfile.get(GLProfile.GL2));
		capabilities.setHardwareAccelerated(true);
		capabilities.setSampleBuffers(true);
		capabilities.setNumSamples(UiConstants.AA_SAMPLES);
		return capabilities;
	}
}
