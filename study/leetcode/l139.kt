////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 *
https://leetcode.com/problems/single-number/description/

136. Single Number
Easy
Topics
Companies
Hint

Given a non-empty array of integers nums, every element appears twice except for one. Find that single one.

You must implement a solution with a linear runtime complexity and use only constant extra space.
 */

class Solution {
    fun singleNumber(nums: IntArray): Int = nums.let {
        it.groupBy { it }
            .filter { it.value.size == 1 }
            .map { it.key }
            .first()
    }

//        fun singleNumber(nums: IntArray): Int {
//            var result = 0
//            for (num in nums) {
//                result^= num
//            }
//            return result
//        }
}

fun main() {

    Solution().singleNumber(intArrayOf(4, 1, 2, 1, 2)).also {
        println("singleNumberIs: $it")
    }
}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////