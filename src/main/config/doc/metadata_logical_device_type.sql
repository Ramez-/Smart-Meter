insert into TechnicalSystem(ID, description) values ('d1227cb2b07c49a1996de30a074afd24', 'DlmsCosem');
insert into TechnicalSystemEngine(ID, messageQueueName, description, maxJobs, pingInterval, statusTID, technicalSystemID) values ('f5aab48b5668403a822c9c9916f4bc92', 'DlmsCosemEngine', null, 30, 100, '2e9593a70e9f4e3aa37dd05d44b4e712', 'd1227cb2b07c49a1996de30a074afd24');

insert into SystemComponentDefinition(ID, name, systemID) VALUES ('25c25b86b95d464fa66f3a36cd7eb414', 'DlmsCosemEngine','d683b9dbcb8f426e947307121d6d86d1');

insert into SystemProperty(ID, keyword, value, dataTypeTID, systemComponentDefinitionID, description) values ('5efefe5e0e324a62add8897670c16c71', 'pollingTaskBufferSynchronizationInterval', '600000', '3c4fe99947994309a2ba99d364379d13', '25c25b86b95d464fa66f3a36cd7eb414', 'Indicates the number of milliseconds until the internal taskcache that stores all prepared tasks is synchronized with db');
insert into SystemProperty(ID, keyword, value, dataTypeTID, systemComponentDefinitionID, description) values ('08de302d66074176b5253f7d6ef6e243', 'executionThreadpoolSize', '10', '3c4fe99947994309a2ba99d364379d13', '25c25b86b95d464fa66f3a36cd7eb414', 'The maximal number of threads used for execution');
insert into SystemProperty(ID, keyword, value, dataTypeTID, systemComponentDefinitionID, description) values ('b43a624d7e834ad39b769b456ab37715', 'maxTasksPerDevice', '1', '3c4fe99947994309a2ba99d364379d13', '25c25b86b95d464fa66f3a36cd7eb414', 'The number of tasks that are allowed to be executed simultaneously on the same device');
insert into SystemProperty(ID, keyword, value, dataTypeTID, systemComponentDefinitionID, description) values ('467dba6deb8d43a0a785167868f7b4fd', 'execPollerStartDelay', '14000', '3c4fe99947994309a2ba99d364379d13', '25c25b86b95d464fa66f3a36cd7eb414', 'initial wait for execution thread');
insert into SystemProperty(ID, keyword, value, dataTypeTID, systemComponentDefinitionID, description) values ('e84ea4d8e005454dbab7b86c231e10c7', 'execPollerInterval', '20000', '3c4fe99947994309a2ba99d364379d13', '25c25b86b95d464fa66f3a36cd7eb414', 'interval for execution thread');
insert into SystemProperty(ID, keyword, value, dataTypeTID, systemComponentDefinitionID, description) values ('e33cdacef1b14bff98c11999892fe602', 'maxPollingResult', '500', '3c4fe99947994309a2ba99d364379d13', '25c25b86b95d464fa66f3a36cd7eb414', 'The maximal number of processing tasks to consider while polling for results');
insert into SystemProperty(ID, keyword, value, dataTypeTID, systemComponentDefinitionID, description) values ('2990dddf636345c89a56ade3c5d8f32d', 'resultThreadpoolSize', '10', '3c4fe99947994309a2ba99d364379d13', '25c25b86b95d464fa66f3a36cd7eb414', null);
insert into SystemProperty(ID, keyword, value, dataTypeTID, systemComponentDefinitionID, description) values ('b39cc538f4f447aba73e6024c9e98304', 'pollingStartDelay', '60000', '3c4fe99947994309a2ba99d364379d13', '25c25b86b95d464fa66f3a36cd7eb414', 'The initial Delay for the polling thread');
insert into SystemProperty(ID, keyword, value, dataTypeTID, systemComponentDefinitionID, description) values ('9ca7946341a344ee8ae5588b4a2b8524', 'pollingInterval', '10000', '3c4fe99947994309a2ba99d364379d13', '25c25b86b95d464fa66f3a36cd7eb414', 'The interval for the polling thread');


insert into TypeCategory (ID, name, description, deprecated, extern) values ('687ed94f558340e681219e9dd884b042', 'TASK_META_INFO_DLMS_COSEM', 'Meta Info for Task', 0, 1);
insert into Type (ID, LID, name, description, value, typeCategoryID, deprecated, extern) values ('44f404d70f6541399d47bd64f124a90c', 02295, 'READ_METER', 'Read Meter', 'Read Meter Dlms Cosem', '687ed94f558340e681219e9dd884b042', 0, 1);



insert into Type (ID, LID, name, description, value, typeCategoryID, deprecated, extern) VALUES ('c56a4ed9a59442e2b4c929716b4a526b', 02708, 'DLMS_Meter', 'DLMS COSEM Meter', 'DLMS_Meter', '4917dbebbcba46e2b2e7f48e3d1130e3', 0, 1);

insert into Firmware (ID, version, description) values ('07fb4928112e4b9e92928f2455248863', 'n.r.', 'n.r');
insert into Model (ID, name, description, deviceCategoryTID, manufacterer) values ('2a92917f5e834eacbbb84066f65795c9', 'L+G E650', 'Landis+Gyr E650', 'c56a4ed9a59442e2b4c929716b4a526b', 'L+G');
insert into DeviceType (ID, modelID, firmwareID) values ('953a7d26259d4b9280786f9ca6332866', '2a92917f5e834eacbbb84066f65795c9', '07fb4928112e4b9e92928f2455248863');


insert into LogicalDeviceType (ID, name, description, roleTID, technicalSystemID, baseLogicalDeviceType, depthToBaseLogicalDeviceType) values ('9a3b6cf1f05d42e19b4e2986f35a01ca', 'L+G DLMS Meter', 'Landis+Gyr DLMS COSEM Meter', '9a4d4480a3984623891eddd0c142c3af', 'd1227cb2b07c49a1996de30a074afd24', '9a3b6cf1f05d42e19b4e2986f35a01ca', 0);
insert into MeterType (logicalDeviceTypeID) values ('9a3b6cf1f05d42e19b4e2986f35a01ca');
insert into AvailableLogicalDeviceType (deviceTypeID, logicalDeviceTypeID) values ('953a7d26259d4b9280786f9ca6332866', '9a3b6cf1f05d42e19b4e2986f35a01ca');

insert into AssignedRegister (meterTypeID, availableRegisterID, readingrelevant, onDemandReadable) values('9a3b6cf1f05d42e19b4e2986f35a01ca','271d879a83d542379d0d6764112d059a',1,1);
insert into AssignedRegister (meterTypeID, availableRegisterID, readingrelevant, onDemandReadable) values('9a3b6cf1f05d42e19b4e2986f35a01ca','d7939778ee834405b832dca5c52ab49a',1,1);
insert into AssignedRegister (meterTypeID, availableRegisterID, readingrelevant, onDemandReadable) values('9a3b6cf1f05d42e19b4e2986f35a01ca','3881ef6b33cb4bc0993ef9b2af812975',1,1);


-- read meter
insert into JobType(ID, executionCommandTID, logicalDeviceTypeID) values ('82bff5e160114f21876afde02393b1d7', 'fe5cd87baa574e38a667b3e18a4420b8', '9a3b6cf1f05d42e19b4e2986f35a01ca');
insert into AssignedTechnicalSystemEngines(jobTypeID, technicalSystemEngineID) values ('82bff5e160114f21876afde02393b1d7', 'f5aab48b5668403a822c9c9916f4bc92');

insert into LogicalDeviceType (ID, name, description, roleTID, technicalSystemID, baseLogicalDeviceType, depthToBaseLogicalDeviceType) values ('18e4fb7d151f40a3b2f95c2fa78a1898', 'L+G DLMS Meter', 'Landis+Gyr DLMS COSEM Meter', 'c960a8efa3534898a8a29b98af878dc8', 'd1227cb2b07c49a1996de30a074afd24', '18e4fb7d151f40a3b2f95c2fa78a1898', 0);
insert into EquipmentType (logicalDeviceTypeID) values ('18e4fb7d151f40a3b2f95c2fa78a1898');
insert into AvailableLogicalDeviceType (deviceTypeID, logicalDeviceTypeID) values ('953a7d26259d4b9280786f9ca6332866', '18e4fb7d151f40a3b2f95c2fa78a1898');

