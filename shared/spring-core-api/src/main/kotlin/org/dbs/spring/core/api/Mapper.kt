package org.dbs.spring.core.api

interface Mapper<Src, Dst> : ApplicationBean {

    override fun initialize() {
        //TODO("Not yet implemented")
    }

    override fun shutdown() {
        //TODO("Not yet implemented")
    }

    //@Deprecated("use copy function")
    fun mapByCopy(src: Src, dst: Dst): Dst
//    fun map(src: Src, classDst: Class<Dst>): Dst =
//
//        classDst.getDeclaredConstructor().newInstance().also {
//            mapByCopy(src, it)
//        }
}
