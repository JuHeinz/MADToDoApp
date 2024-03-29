package org.julheinz.listadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import org.julheinz.entities.ContactEntity;
import org.julheinz.madtodoapp.DetailViewActivity;
import org.julheinz.madtodoapp.R;
import org.julheinz.madtodoapp.databinding.ContactListItemBinding;

import java.util.List;

/**
 * Returns the view for each ContactEntity. Views are used in ListView.
 */
public class ContactListAdapter extends ArrayAdapter<ContactEntity> {

    private final LayoutInflater inflater;

    private final Context parentActivity;

    public ContactListAdapter(Context parent, int layoutIdOfListView, List<ContactEntity> contactList, LayoutInflater inflater) {
        super(parent, layoutIdOfListView, contactList);
        this.inflater = inflater;
        this.parentActivity = parent;
    }

    /**
     * Get the view for a single item by inflating a layout and creating a new view or using a recycled view.
     * Overrides method of ArrayAdapter to produce custom view, not just TextView.
     *
     * @param position                 the position of the item in the list, used to get the ContactEntity object.
     * @param existingViewToBeRecycled view for a contact that can be recycled
     * @param parent                   view that calls this method
     * @return The view for a contact in the list, either created anew or recycled from earlier, layout defined by contact_list_item.xml
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View existingViewToBeRecycled, @NonNull ViewGroup parent) {
        ContactEntity contact = getItem(position); //data to be rendered
        View contactView; //view that displays the data
        ContactListItemBinding contactBinding;

        if (existingViewToBeRecycled != null) {
            contactView = existingViewToBeRecycled; //if there is a view to be recycled, use it.
            //there will always be an already inflated view because of the else statement.

            contactBinding = (ContactListItemBinding) contactView.getTag(); //get data binder from recycled view
            //reset button visibilities so recycled views don't have the states of their previous owners
            contactView.findViewById(R.id.emailBtn).setVisibility(View.VISIBLE);
            contactView.findViewById(R.id.smsBtn).setVisibility(View.VISIBLE);
        } else {
            contactBinding = DataBindingUtil.inflate(inflater, R.layout.contact_list_item, null, false); //if there is no view to be recycled, inflate a new one
            contactView = contactBinding.getRoot(); //assign the view created by data binding inflating to the field.
            contactView.setTag(contactBinding); //set data binding for view
        }

        contactBinding.setContact(contact); //set the "contact" variable for the data binding to the contact entity in this view
        contactBinding.setActivity((DetailViewActivity) parentActivity); //set the "activity" variable for the data binding to the parent view of this list

        if (contact != null && contact.getEmail().isEmpty()) {
            contactView.findViewById(R.id.emailBtn).setVisibility(View.GONE);
        }

        if (contact != null && contact.getPhoneNumber().isEmpty()) {
            contactView.findViewById(R.id.smsBtn).setVisibility(View.GONE);
        }
        return contactView;
    }
}
