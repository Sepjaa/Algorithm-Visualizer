package fi.sepjaa.visualizer.ui.common;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableList;

import net.miginfocom.swing.MigLayout;

/**
 * Main window and entry point for launching the application.
 *
 * @author Jaakko
 *
 */
@SuppressWarnings("serial")
@Component
public class MainWindow extends JFrame implements ApplicationContextAware {
	private static final Logger LOG = LoggerFactory.getLogger(MainWindow.class);
	private final ImmutableList<AlgorithmPanel> panels;

	private ConfigurableApplicationContext ctx;

	@Autowired
	public MainWindow(List<AlgorithmPanel> panels) {
		super();
		this.panels = ImmutableList.copyOf(panels);
	}

	@PostConstruct
	public void init() {
		LOG.info("Showing main window with {} panels", this.panels.size());
		try {
			URL resource = getClass().getClassLoader().getResource("icon.png");
			if (resource != null) {
				File iconFile = new File(resource.getFile());
				Image image = ImageIO.read(iconFile);
				setIconImage(image);
			} else {
				LOG.warn("No icon.png found from resources");
			}
		} catch (IOException e) {
			LOG.warn("Setting icon failed", e);
		}

		setLayout(new MigLayout("insets 0", "[grow, fill]", "[grow, fill]"));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JTabbedPane tabbedPane = new JTabbedPane();
		this.panels.forEach(panel -> {
			tabbedPane.addTab(panel.getTitle(), panel);
		});
		add(tabbedPane);
		setTitle(UiConstants.APPLICATION_TITLE);
		pack();
		// setExtendedState(Frame.MAXIMIZED_BOTH);
		setVisible(true);

	}

	@Override
	public void dispose() {
		super.dispose();
		this.ctx.close();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.ctx = (ConfigurableApplicationContext) applicationContext;
	}

}
