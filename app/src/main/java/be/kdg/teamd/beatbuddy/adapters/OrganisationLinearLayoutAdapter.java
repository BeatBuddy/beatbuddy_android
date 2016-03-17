package be.kdg.teamd.beatbuddy.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import be.kdg.teamd.beatbuddy.R;
import be.kdg.teamd.beatbuddy.model.organisations.Organisation;
import be.kdg.teamd.beatbuddy.util.ImageEncoder;
import butterknife.Bind;
import butterknife.ButterKnife;

public class OrganisationLinearLayoutAdapter {
    private LinearLayout linearLayout;
    private List<Organisation> organisations;
    private Context context;
    private OrganisationClickListener clickListener;

    public OrganisationLinearLayoutAdapter(LinearLayout linearLayout, List<Organisation> organisations, Context context, OrganisationClickListener clickListener) {
        this.linearLayout = linearLayout;
        this.context = context;
        this.clickListener = clickListener;
        this.organisations = organisations;
    }

    public void notifyDataSetChanged() {
        LayoutInflater inflater = LayoutInflater.from(linearLayout.getContext());
        linearLayout.removeAllViews();

        for (final Organisation organisation : organisations) {
            View view = inflater.inflate(R.layout.item_organisation, linearLayout, false);

            linearLayout.addView(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onOrganisationClicked(organisation);
                }
            });
            OrganisationViewHolder viewHolder = new OrganisationViewHolder(view);
            viewHolder.organisationName.setText(organisation.getName());

            if (!TextUtils.isEmpty(organisation.getBannerUrl()))
                Picasso.with(linearLayout.getContext())
                        .load(context.getString(R.string.organisationImageLocation) + ImageEncoder.encodeImageUrl(organisation.getBannerUrl()))
                        .fit()
                        .centerCrop()
                        .into(viewHolder.organisationBanner);
        }

        if(organisations.size() == 0){
            TextView view = new TextView(linearLayout.getContext());
            view.setText(R.string.user_no_organisations);
            linearLayout.addView(view);
        }
    }

    public interface OrganisationClickListener {
        void onOrganisationClicked(Organisation organisation);
    }

    static class OrganisationViewHolder {
        @Bind(R.id.item_organisation_name)
        TextView organisationName;
        @Bind(R.id.item_organisation_banner)
        ImageView organisationBanner;
        @Bind(R.id.item_organisation_users)
        TextView organisationUserCount;

        public OrganisationViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
