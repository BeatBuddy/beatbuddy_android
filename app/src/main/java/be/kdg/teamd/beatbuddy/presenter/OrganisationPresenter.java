package be.kdg.teamd.beatbuddy.presenter;

import be.kdg.teamd.beatbuddy.dal.OrganisationRepository;
import be.kdg.teamd.beatbuddy.model.organisations.Organisation;
import retrofit2.Callback;
import retrofit2.Response;

public class OrganisationPresenter {
    private OrganisationRepository organisationRepository;
    private OrganisationPresenterListener listener;

    public OrganisationPresenter(OrganisationPresenterListener listener, OrganisationRepository organisationRepository) {
        this.listener = listener;
        this.organisationRepository = organisationRepository;
    }

    public void loadOrganisation(long id){
        organisationRepository.getOrganisation(id).enqueue(new Callback<Organisation>() {
            @Override
            public void onResponse(Response<Organisation> response) {
                if(!response.isSuccess()){
                    listener.onException("Error loading organisation. Try again later.");
                    return;
                }

                listener.onOrganisationLoaded(response.body());
            }

            @Override
            public void onFailure(Throwable t) {
                listener.onException("Error loading organisation: " + t.getMessage());
            }
        });
    }

    public interface OrganisationPresenterListener{
        void onOrganisationLoaded(Organisation organisation);
        void onException(String message);
    }
}
