package be.kdg.teamd.beatbuddy.presenter;

import be.kdg.teamd.beatbuddy.dal.OrganisationRepository;
import be.kdg.teamd.beatbuddy.dal.UserRepository;
import be.kdg.teamd.beatbuddy.model.organisations.Organisation;
import be.kdg.teamd.beatbuddy.model.users.User;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateOrganisationPresenter
{
    private OrganisationRepository organisationRepository;
    private CreateOrganisationPresenterListener listener;

    public CreateOrganisationPresenter(CreateOrganisationPresenterListener listener, OrganisationRepository organisationRepository) {
        this.listener = listener;
        this.organisationRepository = organisationRepository;
    }

    public void createOrganisation(String name, String bannerUrl, String colorScheme, String key)
    {
        organisationRepository.createOrganisation(name, bannerUrl, colorScheme, key).enqueue(new Callback<Organisation>()
        {
            @Override
            public void onResponse(Response<Organisation> response)
            {
                if(response.isSuccess())
                    listener.onCreated(response.body());
                else
                    listener.onException("Create failed. Try again later.");
            }

            @Override
            public void onFailure(Throwable t)
            {
                listener.onException("Organisation creation failed. " + t.getMessage());
            }
        });
    }

    public interface CreateOrganisationPresenterListener{
        void onCreated(Organisation organisation);
        void onException(String message);
    }
}
