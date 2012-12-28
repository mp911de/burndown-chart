Burndown Charts from JIRA
=========================

This tiny web app can create burndown charts from JIRA issues. Just drop the WAR into a Java Web-App container like Tomcat, configure it and enjoy the charts!

For the configuration you need two files:

* teams.xml: Team Definitions
* jira-sync.xml: Jira Sync settings

Config
---------
Please set the Java-System-Property `burndown.data.dir` to the base-directory, where you have played the two files `teams.xml`and `jira-sync.xml`

teams.xml
---------
<pre>
<code>
	&lt;teams&gt;
		&lt;team id=&quot;myteam1&quot;&gt;
			&lt;teamSize&gt;5&lt;/teamSize&gt;
			&lt;regularSprintLength&gt;10&lt;/regularSprintLength&gt;
			&lt;regularSprintStart&gt;3&lt;/regularSprintStart&gt;
			&lt;name&gt;The first team&lt;/name&gt;
		&lt;/team&gt;
	&lt;/teams&gt;
</code>
</pre>


Elements:
* team/id: Team-Id. Used also to store local XML data
* teamSize: Size of the team (used to calculare average efforts)
* regularSprintLength: Length of a sprint (workdays, without saturdays/sundays)
* regularSprintStart: Java day of week when the sprint starts
* name: Display-Name


jira-sync.xml
---------
<pre>	
<code>
	&lt;jiraSync&gt;
		&lt;baseUrl&gt;http://my.jira.server&lt;/baseUrl&gt;
		&lt;username&gt;username&lt;/username&gt;
		&lt;password&gt;password&lt;/password&gt;
		&lt;updateMode&gt;FOUR_HOURLY&lt;/updateMode&gt;
	
		&lt;teamSync teamId=&quot;myteam1&quot;&gt;
			&lt;projectKey&gt;JRA&lt;/projectKey&gt;
			&lt;unplanned&gt;true&lt;/unplanned&gt;
			&lt;unplannedFlagFieldId&gt;customfield_10600&lt;/unplannedFlagFieldId&gt;
			&lt;unplannedFlagName&gt;Unplanned&lt;/unplannedFlagName&gt;
			&lt;effortMode&gt;HOURS&lt;/effortMode&gt;
			&lt;storyPointsFieldId&gt;&lt;/storyPointsFieldId&gt;
			&lt;sprintVersionNameScheme&gt;Sprint {0}&lt;/sprintVersionNameScheme&gt;
		&lt;/teamSync&gt;
	&lt;/jiraSync&gt;
</code>
</pre>

Elements:
* updateMode: OFF, HOURLY, FOUR_HOURLY, TWELVE_HOURLY, DAILY
* effortMode: HOURS, STORY_POINTS
