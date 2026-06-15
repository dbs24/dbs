# UserTestContract of family/tree project

## User creation/updating validation

1. There is CreateUserValidationStrategy, which applied for user validation (see property rules: Collection<FieldValidationRule)
2. UserTestContract should append with new unique tests, which validate each dto property (CreateOrUpdateUserCommand).
3. For each dto field, there should be a test waiting for an error if the value contains a value less than or below the minimum length or value.
4. For each dto field, there should be a test waiting for an error if the value contains a value greater than or above the maximum length or maximum value.
5. There should be a test for each dto field that expects an error if the value does not exceed the minimum or maximum length, but does not match the pattern.
6. Should be modified UserRestContractTests, UserGrpcContractTests according new contacts  
7. Modified UserRestContractTests, UserGrpcContractTests must be executed successfully
