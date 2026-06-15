# UserTests migration of family/tree project

## UserTestContract functionality

1. Deprecated tests, which are described in UsersGrpcTests, UsersRestTests, should be ported on new functionality with contracts (UserTestContract)
2. Interface UserTestContract should be appended from UsersGrpcTests, UsersRestTests (unique test only). 
3. Function userTestsFactory should be supplemented with new unique tests ( "Do something via $src")
4. UserRestContractTests, UserGrpcContractTests should be modified, with necessary implementations
5. Modified UserRestContractTests, UserGrpcContractTests must be executed successfully
6. After everything UserRestContractTests, UserGrpcContractTests should be removed


