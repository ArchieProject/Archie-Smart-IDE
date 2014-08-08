
package archie.wizards.newtim;

import java.util.Vector;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class NewTimWizardTemplatesPage extends WizardPage
{
	public static final String PAGE_NAME = "NewTimWizardTemplatesPage";

	Vector<TimTemplate> systemTemplates;
	Vector<TimTemplate> userTemplates;
	Label descriptionTextLabel;
	Group templatesGroup;
	Composite previewImage;

	protected NewTimWizardTemplatesPage()
	{
		super(PAGE_NAME);
		setTitle("Traceability Information Model Wizard (Step 1 of 2)");
		setDescription("Step 1: Pick template");
		systemTemplates = TimTemplatesProvider.getInstance().getSystemTemplates();
		userTemplates = TimTemplatesProvider.getInstance().getUserTemplates();
	}

	@Override
	public void createControl(Composite parent)
	{
		Composite topLevel = new Composite(parent, SWT.NONE);
		GridLayout topLevelLayout = new GridLayout(2, false);
		topLevel.setLayout(topLevelLayout);

		templatesGroup = new Group(topLevel, SWT.V_SCROLL | SWT.H_SCROLL);
		GridData templatesGridData = new GridData();
		templatesGridData.widthHint = 200;
		templatesGridData.heightHint = 330;
		templatesGroup.setLayoutData(templatesGridData);
		templatesGroup.setLayout(new GridLayout());
		templatesGroup.setText("Templates");

		SelectionListener listener = new SelectionListener()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				Button b = (Button) e.getSource();
				TimTemplate template = (TimTemplate) b.getData();
				descriptionTextLabel.setText(template.getDescription());
				previewImage.redraw();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}
		};

		// Load system templates
		for (TimTemplate template : systemTemplates)
		{
			Button radio = new Button(templatesGroup, SWT.RADIO);
			radio.setText(template.getName());
			radio.setData(template);
			radio.addSelectionListener(listener);
		}
		
		// Load user templates
		for (TimTemplate template : userTemplates)
		{
			Button radio = new Button(templatesGroup, SWT.RADIO);
			radio.setText("User: " + template.getName());
			radio.setData(template);
			radio.addSelectionListener(listener);
		}
		
		((Button) templatesGroup.getChildren()[0]).setSelection(true);

		// preview
		Group previewGroup = new Group(topLevel, SWT.NONE);
		GridData gd = new GridData();
		gd.widthHint = 495;
		gd.heightHint = 330;
		previewGroup.setLayoutData(gd);
		previewGroup.setText("Preview");

		previewImage = new Composite(previewGroup, SWT.NONE);
		previewImage.setBounds(10, 17, 480, 320);
		previewImage.addPaintListener(new PaintListener()
		{
			public void paintControl(PaintEvent pe)
			{
				GC gc = pe.gc;
				Image image = getSelectedTemplate().getImage(pe.display);
				gc.drawImage(image, 0, 0);
				image.dispose();
			}
		});

		Group descriptionGroup = new Group(topLevel, SWT.NONE);
		GridData descriptionGroupGridData = new GridData(GridData.FILL_HORIZONTAL);
		descriptionGroupGridData.horizontalSpan = 2;
		descriptionGroup.setLayoutData(descriptionGroupGridData);
		descriptionGroup.setText("Description");

		descriptionTextLabel = new Label(descriptionGroup, SWT.WRAP);
		descriptionTextLabel
				.setText("");
		descriptionTextLabel.setBounds(20, 30, 660, 70);

		setControl(topLevel);
		setPageComplete(true);
	}

	public TimTemplate getSelectedTemplate()
	{
		for (Control c : templatesGroup.getChildren())
		{
			Button b = (Button) c;
			if (b.getSelection())
				return (TimTemplate) b.getData();
		}
		throw new IllegalStateException("At least one radio button should be selected at this point!");
	}
}
