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
	<teams>
		<team id="myteam1">
			<teamSize>5</teamSize>
			<regularSprintLength>10</regularSprintLength>
			<regularSprintStart>3</regularSprintStart>
			<name>The first team</name>
		</team>
	</teams>
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
	<jiraSync>
		<baseUrl>http://my.jira.server</baseUrl>
		<username>username</username>
		<password>password</password>
		<updateMode>FOUR_HOURLY</updateMode>
	
		<teamSync teamId="myteam1">
			<projectKey>JRA</projectKey>
			<unplanned>true</unplanned>
			<unplannedFlagFieldId>customfield_10600</unplannedFlagFieldId>
			<unplannedFlagName>Unplanned</unplannedFlagName>
			<effortMode>HOURS</effortMode>
			<storyPointsFieldId></storyPointsFieldId>
			<sprintVersionNameScheme>Sprint {0}</sprintVersionNameScheme>
		</teamSync>
	</jiraSync>
</pre>

Elements:
* updateMode: OFF, HOURLY, FOUR_HOURLY, TWELVE_HOURLY, DAILY
* effortMode: HOURS, STORY_POINTS
