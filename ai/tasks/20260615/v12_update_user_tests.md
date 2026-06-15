# UserTests migration of family/tree project

## UserTestContract update

1. Extended test form UserGrpcContractTests ("Try to create invalid user via $src", "Try to create user with invalid login via $src" and etc) should be moved to UserTestContract (func userTestsFactory)
2. Extended test form UserRestContractTests ("Try to create invalid user via $src", "Try to create user with invalid email via $src") should be moved to UserTestContract (func userTestsFactory)
3. UserTestContract should be extended of necessary functionality
4. init { } block (UserGrpcContractTests, UserRestContractTests) should contain only single line: include(userTestsFactory(this))
5. Modified UserRestContractTests, UserGrpcContractTests must be executed successfully



