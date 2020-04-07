Java 无限层级的代码片段

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

