package org.vaadin.viritin.it;

import com.vaadin.annotations.Theme;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import org.vaadin.addonhelpers.AbstractTest;
import org.vaadin.viritin.LazyComboBoxUsage;
import org.vaadin.viritin.LazyList;
import org.vaadin.viritin.fields.CaptionGenerator;
import org.vaadin.viritin.fields.LazyComboBox;
import org.vaadin.viritin.fields.MValueChangeEvent;
import org.vaadin.viritin.fields.MValueChangeListener;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.testdomain.Person;

import java.util.List;

@Theme("valo")
public class LazyComboBoxUsageSample extends AbstractTest {

    @Override
    public Component getTestComponent() {

        final LazyComboBoxUsage.LazyService service = new LazyComboBoxUsage.LazyService();
        
        // This is naturally much cleaner with Java 8, just wire to service layer
        // using method reference, with older java, better create a class for 
        // each entity type.
        final LazyComboBox.FilterablePagingProvider filterablePagingProvider = new LazyComboBox.FilterablePagingProvider() {
            
            @Override
            public List findEntities(int firstRow, String filter) {
                System.err.println("find entities " + firstRow + " f: " + filter);
                return service.findPersons(filter, firstRow,
                        LazyList.DEFAULT_PAGE_SIZE);
            }
        };
        final LazyComboBox.FilterableCountProvider filterableCountProvider = new LazyComboBox.FilterableCountProvider() {
            
            @Override
            public int size(String filter) {
                System.err.println("size " + filter);
                return service.countPersons(filter);
            }
            
        };

        final LazyComboBox<Person> cb = new LazyComboBox(Person.class, filterablePagingProvider, filterableCountProvider)
                .setCaptionGenerator(new CaptionGenerator<Person>() {

                    @Override
                    public String getCaption(Person option) {
                        return option.getFirstName() + " " + option.
                        getLastName()
                        + option.getAddresses();
                    }
                });

        cb.addMValueChangeListener(new MValueChangeListener<Person>() {

            @Override
            public void valueChange(MValueChangeEvent<Person> event) {
                Notification.show("Selected value :" + event.getValue());
            }
        });
        
        Button toggle = new Button("Toggle readonly");
        toggle.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                cb.setReadOnly(!cb.isReadOnly());
            }
        });

        return new MVerticalLayout(cb, toggle);
    }

}
