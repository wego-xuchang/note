## Java 无限层级的代码片段



```sql
CREATE TABLE `ge_organization` (
  `ID` varchar(32) NOT NULL,
  `NAME` varchar(255) DEFAULT NULL,
  `PARENT_ID` varchar(32) DEFAULT NULL,
  `CREATE_TIME` bigint(20) NOT NULL,
  `CREATE_USER_ID` varchar(32) NOT NULL,
  `LAST_UPDATE_TIME` bigint(20) DEFAULT NULL,
  `LAST_UPDATE_USER_ID` varchar(32) DEFAULT NULL,
  `ADDRESS` varchar(255) DEFAULT NULL,
  `APPLICATION_ID` varchar(32) DEFAULT NULL,
  `CODE` varchar(32) NOT NULL,
  `CUST_ID` varchar(32) DEFAULT NULL,
  `FAX` varchar(24) DEFAULT NULL,
  `LINK_MAN_DEPT` varchar(64) DEFAULT NULL,
  `LINK_MAN_EMAIL` varchar(255) DEFAULT NULL,
  `LINK_MAN_NAME` varchar(255) DEFAULT NULL,
  `LINK_MAN_POS` varchar(64) DEFAULT NULL,
  `LINK_MAN_TEL` varchar(64) DEFAULT NULL,
  `RANK` int(11) DEFAULT '0',
  `REMARK` varchar(255) DEFAULT NULL,
  `STATUS` int(11) NOT NULL DEFAULT '0',
  `TELEPHONE` varchar(32) DEFAULT NULL,
  `TYPE` int(11) NOT NULL DEFAULT '0',
  `ZIP_CODE` varchar(16) DEFAULT NULL,
  `PATH` varchar(1024) DEFAULT NULL COMMENT '????????',
  `ORGANIZATION_NUMBER` varchar(6) NOT NULL COMMENT '机构编号',
  `ADMIN_USER_ID` varchar(32) DEFAULT NULL COMMENT '管理员id',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `CODE` (`CODE`),
  KEY `IDX_RES_ORGANIZATION` (`CREATE_TIME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```



### 无限层级代码片段

```java
@BusinessParamsValidate(
            argsIndexs = {0, 1}
    )
    @Transactional(
            propagation = Propagation.SUPPORTS,
            readOnly = true
    )
    @Override
    public BusinessResult<List<OrganizationTreeifyResult>> treeifyOrganization(OrganizationTreeifyParam param, BusinessCallContext businessContext) {
        try {
            OrganizationQueryDTO organizationQuery = new OrganizationQueryDTO();
            BeanCopyUtils.copyProperties(param,organizationQuery);
            List<OrganizationResultDTO> organizationResultList = organizationManager.query(organizationQuery);

            Map<String,OrganizationTreeifyResult> map = new HashMap<>();
            List<OrganizationTreeifyResult> firstLevel = new ArrayList<>();

            for (OrganizationResultDTO organizationResultDTO : organizationResultList){
                OrganizationTreeifyResult organizationTreeifyResult = new OrganizationTreeifyResult();
                BeanCopyUtils.copyProperties(organizationResultDTO,organizationTreeifyResult);
                map.put(organizationResultDTO.getId(),organizationTreeifyResult);
                if (StringUtils.isEmpty(organizationResultDTO.getParentId())){
                    firstLevel.add(organizationTreeifyResult);
                }
            }

            for (OrganizationResultDTO organizationResultDTO : organizationResultList){
                if (StringUtils.isEmpty(organizationResultDTO.getParentId())){
                    continue;
                }
                OrganizationTreeifyResult parentOrganizationResult = map.get(organizationResultDTO.getParentId());
                OrganizationTreeifyResult subOrganizationResult = map.get(organizationResultDTO.getId());
                parentOrganizationResult.getSubOrganization().add(subOrganizationResult);
            }
            return BusinessResult.success(firstLevel);
        }catch (Exception e) {
            this.logger.error("机构树形查询失败，原因:", e);
            throw new AppException(e);
        }
    }
```



### 已知节点，获取该节点的子节点（包括本身）

```java
//开始片段
UserRoleDataQueryParam userRoleDataQueryParam = new UserRoleDataQueryParam();
            userRoleDataQueryParam.setUserId(userId);
            if(!userRoleService.verifySuperAdminRole(userRoleDataQueryParam).getData()){

                List<RoleResultDTO> roleResultList = this.getUserAllRoles(userId);
                List<String> userRoleIds = new ArrayList<>();
                if (CollectionUtils.isEmpty(roleResultList)){
                    return BusinessResult.fail("50009385", this.getMessage("50009385"));
                }
                for (RoleResultDTO roleResult : roleResultList){
                    if (!userRoleIds.contains(roleResult.getId())){
                        userRoleIds.add(roleResult.getId());
                    }
                }
                if (!userRoleIds.contains(param.getParentId())){
                    return BusinessResult.fail("50009386", this.getMessage("50009386"));
                }
            }

//getUserAllRoles
   private List<RoleResultDTO> getUserAllRoles(String userId){
        UserRoleQueryDTO userRoleQuery = new UserRoleQueryDTO();
        userRoleQuery.setUserId(userId);
        List<String> roleIds = new ArrayList<>();
        try {
            List<UserRoleResultDTO> userRoleResultList = userRoleManager.query(userRoleQuery);
            for (UserRoleResultDTO userRole : userRoleResultList){
                if (!roleIds.contains(userRole.getRoleId())){
                    roleIds.add(userRole.getRoleId());
                }
            }
        }catch (Exception e) {
            this.logger.error("查询用户角色信息失败，原因：", e);
            throw new AppException(e);
        }
        List<RoleResultDTO> result = new ArrayList<>();
        try {
            RoleGetsDTO roleGets = new RoleGetsDTO();
            List<RoleResultDTO> roleResultList = roleInfoManager.gets(roleGets);
            for (String roleId : roleIds){
                this.getAllChildren(result,roleResultList,roleId);
            }
        }catch (Exception e) {
            this.logger.error("查询角色信息失败，原因：", e);
            throw new AppException(e);
        }
        result.stream().collect(Collectors.collectingAndThen(
                Collectors.toCollection(() -> new TreeSet<>(
                        Comparator.comparing(RoleResultDTO::getId))), ArrayList::new));
        return result;
    }
//getAllChildren
private List<RoleResultDTO> getAllChildren(List<RoleResultDTO> children,List<RoleResultDTO> allRoleResultList,String roleId){
        List<RoleResultDTO> tempList = new ArrayList<>();
        for (RoleResultDTO role : allRoleResultList) {
            if (StringUtils.equals(roleId,role.getParentId())){
                tempList.add(role);
            }
            if (StringUtils.equals(roleId,role.getId())){
                children.add(role);
            }
        }
        if (!CollectionUtils.isEmpty(tempList)){
            for (RoleResultDTO role : tempList) {
                children.add(role);
                String temp = role.getId();
                getAllChildren(children,allRoleResultList,temp);
            }
        }
        return  children;
    }
```



### 已知节点，获取该节点的父节点（包括本身）

```java
private List<String> getOrgFilterResult(List<String> organizationIds) {
        if (CollectionUtils.isEmpty(organizationIds)){
            return organizationIds;
        }
        try {
            List<OrganizationResultDTO> organizationResultList =  organizationManager.query(new OrganizationQueryDTO());
            Map<String,OrganizationResultDTO> organizationMap = organizationResultList.stream().collect(Collectors.toMap(OrganizationResultDTO::getId, Function.identity(), (key1, key2) -> key2));

            List<String> pids = new ArrayList<>();
            for (String orgId : organizationIds){
                if (organizationMap.containsKey(orgId)){
                    OrganizationResultDTO organizationResult = organizationMap.get(orgId);
                    String pid = organizationResult.getParentId();
                    while (!StringUtils.isEmpty(pid)){
                        if(!pids.contains(pid)) {
                            pids.add(pid);
                        }
                        if (organizationMap.containsKey(pid)){
                            pid = organizationMap.get(pid).getParentId();
                        }
                    }
                }
            }
            if (!CollectionUtils.isEmpty(pids)){
                organizationIds.addAll(pids);
            }
        } catch (Exception e) {
            this.logger.error("根据条件查询组织机构或部门信息失败，原因：", e);
            throw new AppException(e);
        }

        return organizationIds;
    }
```

这种方式不可以，出现多个同级的父

```java
private List<OrganizationDataResult> getOrgsResult(List<String> organizationIds,List<OrganizationResultDTO> organizationResultList,Map<String,OrganizationResultDTO> organizationMap) {
        if (CollectionUtils.isEmpty(organizationIds)){
            return new ArrayList<>();
        }
        try {
            List<OrganizationDataResult> result= new ArrayList<>();
            for (String orgId : organizationIds){
                if (organizationMap.containsKey(orgId)){
                    OrganizationResultDTO organizationResult = organizationMap.get(orgId);
                    OrganizationDataResult subDataOrg = new OrganizationDataResult();
                    subDataOrg.setParentId(organizationResult.getParentId());
                    subDataOrg.setName(organizationResult.getName());
                    subDataOrg.setId(organizationResult.getId());
                    subDataOrg.setType(FsnshConstants.RoleDataDetailTypeEnum.ORG.getCode());
                    subDataOrg.setLabel(FsnshConstants.ProductLabelEnum.ORG.getCode());
                    String pid = organizationResult.getParentId();
//                    result.add(subDataOrg);

                    while (!StringUtils.isEmpty(pid)){
                        if (organizationMap.containsKey(pid)){
                            OrganizationResultDTO parentResult = organizationMap.get(pid);
                            OrganizationDataResult parentDataOrg = new OrganizationDataResult();
                            parentDataOrg.setParentId(parentResult.getParentId());
                            parentDataOrg.setName(parentResult.getName());
                            parentDataOrg.setId(parentResult.getId());
                            parentDataOrg.setType(FsnshConstants.RoleDataDetailTypeEnum.ORG.getCode());
                            parentDataOrg.setLabel(FsnshConstants.ProductLabelEnum.ORG.getCode());
                            pid = parentResult.getParentId();
                            parentDataOrg.getChildren().add(subDataOrg);

                            subDataOrg = parentDataOrg;
                        }
                    }
                    result.add(subDataOrg);
                }
            }
            return result;
        } catch (Exception e) {
            this.logger.error("根据条件查询组织机构或部门信息失败，原因：", e);
            throw new AppException(e);
        }

    }
```

