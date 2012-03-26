package de.paluch.burndown.jsf.impl;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.paluch.burndown.DataAccess;
import de.paluch.burndown.model.Sprint;
import de.paluch.burndown.model.SprintEffort;
import de.paluch.burndown.sync.jira.JiraSyncException;
import de.paluch.burndown.sync.jira.SingleJiraSync;

/**
 * <br>
 * <br>
 * Project: burdnown-chart <br>
 * Autor: mark <br>
 * Created: 21.03.2012 <br>
 * <br>
 */
@ManagedBean
@RequestScoped
public class SprintController {

    public static DataAccess DATA_ACCESS = new DataAccess();
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @ManagedProperty("sprintListModel")
    private SprintListModel sprintListModel;

    /**
     * @return the sprintListModel
     */
    public SprintListModel getSprintListModel() {

        return sprintListModel;
    }

    public void save() {

        if (!sprintListModel.getOldStartDate().equals(sprintListModel.getSprint().getStartDate())) {
            shiftSprintDays(sprintListModel.getSprint());
        }

        SprintController.DATA_ACCESS.storeSprint(sprintListModel.getTeam().getId(), sprintListModel.getSprint());
        sprintListModel.setNewSprint(false);
        sprintListModel.setOldStartDate(sprintListModel.getSprint().getStartDate());
    }

    public void jiraSync() {
        SingleJiraSync sync = new SingleJiraSync();
        try {
            sync.syncSprint(sprintListModel.getTeam().getId(), sprintListModel.getSprint());
        } catch (JiraSyncException e) {
            FacesContext.getCurrentInstance().addMessage("sync",
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Sync Failed", e.getMessage()));
            logger.warn("Sync Failed", e);
        }
    }

    /**
     * @param sprintListModel
     *            the sprintListModel to set
     */
    public void setSprintListModel(SprintListModel sprintListModel) {

        this.sprintListModel = sprintListModel;
    }

    /**
     * @param sprint
     */
    private void shiftSprintDays(Sprint sprint) {

        Date startDate = sprint.getStartDate();
        Date firstDate = sprint.getEffort().get(0).getDate();
        long difference = startDate.getTime() - firstDate.getTime();

        for (SprintEffort effort : sprint.getEffort()) {
            effort.setDate(new Date(effort.getDate().getTime() + difference));
        }

    }

}
