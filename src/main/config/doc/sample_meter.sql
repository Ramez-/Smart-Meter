declare @tenantName varchar(max) = 'DEMO';
declare @tenantId varchar(32);

set @tenantId = (select ID from Tenant where name = @tenantName);

-- L+G DLMS COSEM Meter
insert into Device (
  ID,
  description,
  serialNumber,
  timeZone,
  errorStatusTID,
  statusTID,
  workflowTID,
  operatingStatusTID,
  deviceTypeID,
  tenantID
) 
values (
  'd08de70ada6e4dea95d17bac8fd97d93', 
  'L+G E650', 
  '50725713',
  3600000,
  'f04779ea9b5f40d4894239e9953bd4f4',
  '165e0668bc0842efa73f754a4cd6c164', 
  'c682a9318c9c480fae97c2d603025d12', 
  'c777744099ff4c9ca449feea77e82dd5', 
  '953a7d26259d4b9280786f9ca6332866', 
  @tenantId 
);

insert into LogicalDevice (
  ID, 
  roleTID, 
  deviceID, 
  logicalDeviceTypeID, 
  tenantID
)
values (
  '06cb59fc78fc41d79d20fc8b70977786', 
  '9a4d4480a3984623891eddd0c142c3af', 
  'd08de70ada6e4dea95d17bac8fd97d93', 
  '9a3b6cf1f05d42e19b4e2986f35a01ca', 
  @tenantId
);

insert into Meter (
  logicalDeviceID, 
  switchingStatusTID, 
  loadStatusTID, 
  loadProfileReadoutStatusTID,
  prepayCreditStatusTID,
  powerQualityDataReadoutStatusTID
) 
values (
  '06cb59fc78fc41d79d20fc8b70977786', 
  'e9a5f87ac002455d809e7886aeba6d52', 
  'aeddcb39771d43cc9dac9b2548c2f3a3', 
  'c1a41425475e4272a6d1d4d06f56d7d8', 
  '59122f088b944dd7b62c3a0a8a09173b', 
  'dcd0d07ce91b4ce680d6a81397f6c3d8'
);

insert into LogicalDevice (
  ID, 
  roleTID, 
  deviceID, 
  logicalDeviceTypeID, 
  tenantID
) 
values (
  'b651a76b2d8d47a19d9a8be16704c6c2', 
  'c960a8efa3534898a8a29b98af878dc8', 
  'd08de70ada6e4dea95d17bac8fd97d93', 
  '18e4fb7d151f40a3b2f95c2fa78a1898', 
  @tenantId
);

insert into Equipment (
  logicalDeviceID
) 
values (
  'b651a76b2d8d47a19d9a8be16704c6c2'
);
