package org.julheinz.contacts;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.julheinz.entities.ContactEntity;
import org.julheinz.madtodoapp.R;



import java.util.List;

public class ContactListAdapter extends ArrayAdapter<ContactEntity> {

    private final LayoutInflater inflater;
    ContactEntity contact;

    public ContactListAdapter(Context parent, int layoutIdOfListView, List<ContactEntity> contactList, LayoutInflater inflater) {
        super(parent, layoutIdOfListView, contactList);
        this.inflater = inflater;
    }

    /**
     * Get the view for a single item by inflating a layout and creating a new view or using a recycled view.
     * Overrides method of ArrayAdapter produce custom view, not just TextView.
     * @param position the position of  in the list, used to get the ContactEntity object.
     * @param existingViewToBeRecycled view for a contact that can be recycled
     * @param parent view that calls this method
     * @return The view for a task in the list, either created anew or recycled from earlier, layout defined by contact_list_item.xml
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View existingViewToBeRecycled, @NonNull ViewGroup parent) {

        contact = getItem(position); //data to be rendered
        View contactView; //view that displays the data
        contactView = inflater.inflate(R.layout.contact_list_item, null, false); //if there is a view to be recycled, use it.
        TextView nameTextView = contactView.findViewById(R.id.nameOutput);
        nameTextView.setText(contact.getName());
        return contactView;
    }


}
