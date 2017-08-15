package com.baliset;


import com.baliset.conf.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.builder.*;
import org.springframework.context.*;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;


@Configuration
@EnableAutoConfiguration
@ComponentScan({"com.baliset"}) // scan both package trees for components

@EnableScheduling
public class SwingLabApp extends JFrame
{
  private int sectorItems_;
  private int sectorCount_;

  private boolean optionSnaking_;
  private boolean optionDepth_;
  private boolean optionMulti_;
  private boolean optionHeadersForMulti_;
  final String kItemCountPerSector = "# Items/Sector: ";
  final String kSectorCount = "# Sectors: ";

  private void assignAction(int key, int modifier, SwingLabAction sa, Action action)
  {
    JPanel jc = (JPanel) this.getContentPane();
    ActionMap am = jc.getActionMap();

    am.put(sa.name(), action);

    KeyStroke ks;

    jc.registerKeyboardAction(action, sa.name(),
        KeyStroke.getKeyStroke(key, modifier), JComponent.WHEN_IN_FOCUSED_WINDOW);

  }

  @Autowired SwingLabDefaultsConfig swingLabDefaultsConfig;

  private void setup(int initialRows, int initialSectors)
  {
    sectorItems_ = initialRows;
    sectorCount_ = initialSectors;
    optionSnaking_ = true;
    optionDepth_ = true;
    optionMulti_ = true;
    optionHeadersForMulti_ = true;


    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    int kWidth = 400;
    int kHeight = 400;
    setSize(kWidth, kHeight);


    final JCheckBox snaking = new JCheckBox("Snaking", optionSnaking_);
    final JCheckBox depth = new JCheckBox("Depth", optionDepth_);
    final JCheckBox multi = new JCheckBox("MultiTables", optionMulti_);
    final JCheckBox headersForMulti = new JCheckBox("Add header labels", optionHeadersForMulti_);

    final JSlider sliderItems = new JSlider(SwingConstants.HORIZONTAL, 0, 1000, sectorItems_);
    final JSlider sliderSectors = new JSlider(SwingConstants.HORIZONTAL, 0, 100, sectorCount_);

    final JLabel labelSectors = new JLabel(kSectorCount + new Integer(sectorCount_).toString());
    final JLabel labelItems = new JLabel(kItemCountPerSector + new Integer(sectorItems_).toString());


    sliderSectors.setEnabled(optionMulti_);

    ChangeListener sliderItemsListener = new ChangeListener()
    {
      @Override
      public void stateChanged(ChangeEvent event)
      {
        JSlider slider = (JSlider) event.getSource();

        sectorItems_ = slider.getValue();
        labelItems.setText(kItemCountPerSector + new Integer(sectorItems_).toString());
      }
    };

    ChangeListener sliderSectorsListener = new ChangeListener()
    {
      @Override
      public void stateChanged(ChangeEvent event)
      {
        JSlider slider = (JSlider) event.getSource();

        sectorCount_ = slider.getValue();
        labelSectors.setText(kSectorCount + new Integer(sectorCount_).toString());

      }
    };


    headersForMulti.setEnabled(optionMulti_);

    ActionListener snakeActionListener = new ActionListener()
    {
      public void actionPerformed(ActionEvent actionEvent)
      {
        AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
        optionSnaking_ = abstractButton.getModel().isSelected();
      }
    };

    ActionListener depthActionListener = new ActionListener()
    {
      public void actionPerformed(ActionEvent actionEvent)
      {
        AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
        optionDepth_ = abstractButton.getModel().isSelected();
      }
    };
    ActionListener multiActionListener = new ActionListener()
    {
      public void actionPerformed(ActionEvent actionEvent)
      {
        AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
        optionMulti_ = abstractButton.getModel().isSelected();
        headersForMulti.setEnabled(optionMulti_);
        sliderSectors.setEnabled(optionMulti_);
      }
    };

    ActionListener headersForMultiActionListener = new ActionListener()
    {
      public void actionPerformed(ActionEvent actionEvent)
      {
        AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
        optionHeadersForMulti_ = abstractButton.getModel().isSelected();
      }
    };


    sliderItems.addChangeListener(sliderItemsListener);
    sliderSectors.addChangeListener(sliderSectorsListener);

    snaking.addActionListener(snakeActionListener);
    depth.addActionListener(depthActionListener);
    multi.addActionListener(multiActionListener);
    headersForMulti.addActionListener(headersForMultiActionListener);

    JPanel optionsPanel = new JPanel();
    optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));

    Border optionsBorder = BorderFactory.createTitledBorder("Options");
    Border buttonsBorder = BorderFactory.createTitledBorder("Actions");

    optionsPanel.setBorder(optionsBorder);
    // optionsPanel.setPreferredSize(new Dimension(300,120)) ;

    optionsPanel.add(labelItems);
    optionsPanel.add(sliderItems);

    optionsPanel.add(snaking);
    optionsPanel.add(depth);
    optionsPanel.add(multi);
    optionsPanel.add(headersForMulti);
    optionsPanel.add(labelSectors);

    optionsPanel.add(sliderSectors);     // number of sectors

    JPanel execPanel = new JPanel();
    execPanel.setBorder(buttonsBorder);
    execPanel.setPreferredSize(new Dimension(300, 100));
    JButton launch = createLaunchButton();
    execPanel.add(launch);

    Container contentPane = getContentPane();
    contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
    contentPane.add(optionsPanel);
    contentPane.add(execPanel);

    pack();
    // setMinimumSize(new Dimension(200,200));
    setLocationByPlatform(true);
    setVisible(true);


  }

  public SwingLabApp()
  {
    //----- very basic Swing App initialization -----
    super("SwingLabApp");



//    setup(swingLabDefaultsConfig.initialRows, swingLabDefaultsConfig.initialSectors);
    setup(20, 3);

  }


  private JButton createLaunchButton()
  {
    SwingLabAction sa = SwingLabAction.Launch;

    AbstractAction action = new AbstractAction(sa.getLabel())
    {
      public void actionPerformed(ActionEvent e)
      {
        new SwingLab(sectorItems_, sectorCount_, optionSnaking_, optionDepth_, optionMulti_, optionHeadersForMulti_);
      }
    };

    // assignAction(KeyEvent.VK_A, 0, sa, appendItemAction);

    return new JButton(action);
  }


  private static void lookAndFeel()
  {
    try {
      // Set cross-platform Java L&F (also called "Metal")
      UIManager.setLookAndFeel(
          UIManager.getCrossPlatformLookAndFeelClassName());
    } catch (UnsupportedLookAndFeelException e) {
      // handle exception
    } catch (ClassNotFoundException e) {
      // handle exception
    } catch (InstantiationException e) {
      // handle exception
    } catch (IllegalAccessException e) {
      // handle exception
    }
  }

  public static void main(String[] args)
  {
    lookAndFeel();



  //  JFrame appFrame = new SwingLabApp(initialRows, initialSectors);



    ConfigurableApplicationContext ctx = new SpringApplicationBuilder(SwingLabApp.class)
        .headless(false).run(args);

    EventQueue.invokeLater(() -> {
      SwingLabApp ex = ctx.getBean(SwingLabApp.class);
      ex.setVisible(true);
    });


  }


}
