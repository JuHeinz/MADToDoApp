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


public class ContactListAdapter extends ArrayAdapter<ContactEntity> {

    private final LayoutInflater inflater;

    private final Context parentActivity;

    public ContactListAdapter(Context parent, int layoutIdOfListView, List<ContactEntity> contactList, LayoutInflater inflater) {
        super(parent, layoutIdOfListView, contactList);
        this.inflater = inflater;
        this.parentActivity = parent;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View existingViewToBeRecycled, @NonNull ViewGroup parent) {
        ContactEntity contact = getItem(position);
        View contactView;
        ContactListItemBinding contactBinding;

        if (existingViewToBeRecycled != null) {
            contactView = existingViewToBeRecycled;


            contactBinding = (ContactListItemBinding) contactView.getTag();

            contactView.findViewById(R.id.emailBtn).setVisibility(View.VISIBLE);
            contactView.findViewById(R.id.smsBtn).setVisibility(View.VISIBLE);
        } else {
            contactBinding = DataBindingUtil.inflate(inflater, R.layout.contact_list_item, null, false);
            contactView = contactBinding.getRoot();
            contactView.setTag(contactBinding);
        }

        contactBinding.setContact(contact);
        contactBinding.setActivity((DetailViewActivity) parentActivity);

        if (contact != null && contact.getEmail().isEmpty()) {
            contactView.findViewById(R.id.emailBtn).setVisibility(View.GONE);
        }

        if (contact != null && contact.getPhoneNumber().isEmpty()) {
            contactView.findViewById(R.id.smsBtn).setVisibility(View.GONE);
        }
        return contactView;
    }
}
