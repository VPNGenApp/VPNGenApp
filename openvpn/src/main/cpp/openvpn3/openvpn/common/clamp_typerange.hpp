//    OpenVPN -- An application to securely tunnel IP networks
//               over a single port, with support for SSL/TLS-based
//               session authentication and key exchange,
//               packet encryption, packet authentication, and
//               packet compression.
//
//    Copyright (C) 2023 OpenVPN Inc.
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Affero General Public License Version 3
//    as published by the Free Software Foundation.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Affero General Public License for more details.
//
//    You should have received a copy of the GNU Affero General Public License
//    along with this program in the COPYING file.
//    If not, see <http://www.gnu.org/licenses/>.


#pragma once

#include <limits>
#include <algorithm>
#include <functional>

#include "numeric_util.hpp"

namespace openvpn::numeric_util {

/* ============================================================================================================= */
//  clamp_to_typerange
/* ============================================================================================================= */

/**
 * @brief Clamps the input value to the legal range for the output type
 *
 * @tparam OutT Output type
 * @tparam InT  Input type
 * @param inVal Input value
 * @return OutT safely converted from InT, range limited to fit.
 */
template <typename OutT, typename InT>
OutT clamp_to_typerange(InT inVal)
{
    if constexpr (numeric_util::is_int_rangesafe<OutT, InT>())
    {
        return static_cast<OutT>(inVal);
    }
    else if constexpr (numeric_util::is_int_u2s<OutT, InT>())
    {
        auto unsignedInVal = static_cast<uintmax_t>(inVal);
        return static_cast<OutT>(std::min(static_cast<uintmax_t>(std::numeric_limits<OutT>::max()), unsignedInVal));
    }
    else if constexpr (numeric_util::is_int_s2u<OutT, InT>())
    {
        auto lowerVal = static_cast<uintmax_t>(std::max(inVal, 0));
        auto upperLimit = static_cast<uintmax_t>(std::numeric_limits<OutT>::max());
        return static_cast<OutT>(std::min(lowerVal, upperLimit));
    }
    else
    {
        auto outMin = static_cast<InT>(std::numeric_limits<OutT>::min());
        auto outMax = static_cast<InT>(std::numeric_limits<OutT>::max());
        return static_cast<OutT>(std::clamp(inVal, outMin, outMax));
    }
}

/* ============================================================================================================= */
//  clamp_to_default
/* ============================================================================================================= */

/**
 * @brief Adjusts the input value to the default if the input value exceeds the range of the output type
 *
 * @tparam OutT Output type
 * @tparam InT  Input type
 * @param inVal Input value
 * @param defVal Input value
 * @return OutT safely converted from InT, potentially adjusted
 */

template <typename OutT, typename InT>
OutT clamp_to_default(InT inVal, OutT defVal)
{
    if constexpr (!numeric_util::is_int_rangesafe<OutT, InT>())
    {
        if (!is_safe_conversion<OutT>(inVal))
            return defVal;
    }

    return static_cast<OutT>(inVal);
}

/* ============================================================================================================= */
//  clamp_notify
/* ============================================================================================================= */

/**
 * @brief Calls FuncT cb if the input value exceeds the range of the output type.
 *
 * @tparam OutT Output type
 * @tparam InT  Input type
 * @tparam FuncT Invokable type that accepts InT and returns OutT
 * @param inVal Input value
 * @param cb Notification callback
 * @return Result of the FuncT callback
 */

template <typename OutT, typename InT, typename FuncT>
OutT clamp_notify(InT inVal, FuncT cb)
{
    if constexpr (!numeric_util::is_int_rangesafe<OutT, InT>())
    {
        if (!is_safe_conversion<OutT>(inVal))
            return std::invoke(cb, inVal);
    }

    return static_cast<OutT>(inVal);
}

} // namespace openvpn::numeric_util
